package spark.jobserver

import java.io.File
import java.net.URL
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import spark.jobserver.api.JSparkJob
import spark.jobserver.cache.Cache
import spark.jobserver.io.JobDAOActor.BinaryPath
import spark.jobserver.io.{BinaryType, JobDAOActor}
import spark.jobserver.util.{ContextURLClassLoader, JarUtils}

class JobCacheImpl(cacheConfig: Config, dao: ActorRef, ctx: SparkContext, loader: ContextURLClassLoader)
  extends JobCache {

  type CacheType = Cache[String, SparkJobInfo]

  implicit private val daoAskTimeout: Timeout = Timeout(3 seconds)
  private val cacheDriver = Try(cacheConfig.getString("driver")).getOrElse("spark.jobserver.cache.LRUCache")
  private val cacheEnabled = Try(cacheConfig.getBoolean("enabled")).getOrElse(true)
  private val logger = LoggerFactory.getLogger(getClass)
  private val cache = JarUtils.loadClassWithArgs[CacheType](cacheDriver, cacheConfig)

  private def getJarPath(name: String, uploadTime: DateTime): Future[BinaryPath] = {
    (dao ? JobDAOActor.GetBinaryPath(name, BinaryType.Jar, uploadTime)).mapTo[JobDAOActor.BinaryPath]
  }

  private def generateCacheId(name: String, uploadTime: DateTime, classPath: String): String = {
    UUID.nameUUIDFromBytes(s"$name${uploadTime.toString}$classPath".getBytes("UTF-8")).toString
  }

  private def getJavaViaDao(appName: String, uploadTime: DateTime, classPath: String): Future[JavaJarInfo] = {
    getJarPath(appName, uploadTime)
      .map(j => j.binPath)
      .map(f => new File(f).getAbsolutePath)
      .map { path =>
        ctx.addJar(path)
        loader.addURL(new URL("file:" + path))
        val constructor = Try(JarUtils.loadClassOrObject[JSparkJob[_, _]](classPath, loader)).get
        JavaJarInfo(() => constructor, classPath, path)
      }
  }

  private def getJobViaDao(appName: String, uploadTime: DateTime, classPath: String): Future[JobJarInfo] = {
    getJarPath(appName, uploadTime)
      .map(j => j.binPath)
      .map(f => new File(f).getAbsolutePath)
      .map { path =>
        ctx.addJar(path)
        loader.addURL(new URL("file:" + path))
        val constructor = Try(JarUtils.loadClassOrObject[api.SparkJobBase](classPath, loader)).get
        JobJarInfo(() => constructor, classPath, path)
      }
  }

  def getSparkJob(appName: String, uploadTime: DateTime, classPath: String): JobJarInfo = {
    logger.info(s"Loading app: $appName at $uploadTime")
    if (cacheEnabled) {
      cache.getOrPut(
        generateCacheId(appName, uploadTime, classPath),
        Await.result(getJobViaDao(appName, uploadTime, classPath), 3 seconds)
      ).asInstanceOf[JobJarInfo]
    } else {
      Await.result(getJobViaDao(appName, uploadTime, classPath), daoAskTimeout.duration)
    }
  }

  def getJavaJob(appName: String, uploadTime: DateTime, classPath: String): JavaJarInfo = {
    logger.info(s"Loading app: $appName at $uploadTime")
    if (cacheEnabled) {
      cache.getOrPut(
        generateCacheId(appName, uploadTime, classPath),
        Await.result(getJavaViaDao(appName, uploadTime, classPath), daoAskTimeout.duration)
      ).asInstanceOf[JavaJarInfo]
    } else {
      Await.result(getJavaViaDao(appName, uploadTime, classPath), daoAskTimeout.duration)
    }
  }

  def getPythonJob(appName: String, uploadTime: DateTime, classPath: String): PythonJobInfo = {
    cache.getOrPut(generateCacheId(appName, uploadTime, classPath), {
      val pyPathReq =
        (dao ? JobDAOActor.GetBinaryPath(appName, BinaryType.Egg, uploadTime)).mapTo[JobDAOActor.BinaryPath]
      val pyPath = Await.result(pyPathReq, daoAskTimeout.duration).binPath
      val pyFilePath = new java.io.File(pyPath).getAbsolutePath
      PythonJobInfo(pyFilePath)
    }).asInstanceOf[PythonJobInfo]
  }

}