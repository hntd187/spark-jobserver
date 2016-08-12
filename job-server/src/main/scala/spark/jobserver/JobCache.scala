package spark.jobserver

import java.net.URL

import scala.concurrent.Await

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.SparkContext
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import spark.jobserver.cache.LRUCache
import spark.jobserver.io.{BinaryType, JobDAOActor}
import spark.jobserver.util.{ContextURLClassLoader, JarUtils}

/**
 * A cache for SparkJob classes.  A lot of times jobs are run repeatedly, and especially for low-latency
 * jobs, why retrieve the binary and load it every single time?
 */

class JobCacheImpl(maxEntries: Int,
                   dao: ActorRef,
                   sparkContext: SparkContext,
                   loader: ContextURLClassLoader) extends JobCache {
  import scala.concurrent.duration._

  private val cache = new LRUCache[(String, DateTime, String, BinaryType), BinaryJobInfo](maxEntries)
  private val logger = LoggerFactory.getLogger(getClass)
  implicit val daoAskTimeout: Timeout = Timeout(3 seconds)

  /**
   * Retrieves the given SparkJob class from the cache if it's there, otherwise use the DAO to retrieve it.
   * @param appName the appName under which the binary was uploaded
   * @param uploadTime the upload time for the version of the binary wanted
   * @param classPath the fully qualified name of the class/object to load
   */
  def getSparkJob(appName: String, uploadTime: DateTime, classPath: String): JobJarInfo = {
    cache.get((appName, uploadTime, classPath, BinaryType.Jar), {
      val jarPathReq =
        (dao ? JobDAOActor.GetBinaryPath(appName, BinaryType.Jar, uploadTime)).mapTo[JobDAOActor.BinaryPath]
      val jarPath = Await.result(jarPathReq, daoAskTimeout.duration).binPath
      val jarFilePath = new java.io.File(jarPath).getAbsolutePath()
      sparkContext.addJar(jarFilePath) // Adds jar for remote executors
      loader.addURL(new URL("file:" + jarFilePath)) // Now jar added for local loader
      val constructor = JarUtils.loadClassOrObject[spark.jobserver.api.SparkJobBase](classPath, loader)
      JobJarInfo(constructor, classPath, jarFilePath)
    }).asInstanceOf[JobJarInfo]
  }

  /**
    * Retrieves a Python job egg location from the cache if it's there, otherwise use the DAO to retrieve it.
    * @param appName the appName under which the binary was uploaded
    * @param uploadTime the upload time for the version of the binary wanted
    * @param classPath the fully qualified name of the class/object to load
    * @return The case class containing the location of the binary file for the specified job.
    */
  override def getPythonJob(appName: String, uploadTime: DateTime, classPath: String): PythonJobInfo = {
    cache.get((appName, uploadTime, classPath, BinaryType.Egg), {
      val pyPathReq =
        (dao ? JobDAOActor.GetBinaryPath(appName, BinaryType.Egg, uploadTime)).mapTo[JobDAOActor.BinaryPath]
      val pyPath = Await.result(pyPathReq, daoAskTimeout.duration).binPath
      val pyFilePath = new java.io.File(pyPath).getAbsolutePath()
      PythonJobInfo(pyFilePath)
    }).asInstanceOf[PythonJobInfo]
  }
}
