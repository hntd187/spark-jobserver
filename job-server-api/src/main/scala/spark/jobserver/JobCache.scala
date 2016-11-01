package spark.jobserver

import org.joda.time.DateTime

trait BinaryJobInfo
trait SparkJobInfo

case class JobJarInfo(constructor: () => api.SparkJobBase,
                      className: String,
                      jarFilePath: String) extends BinaryJobInfo with SparkJobInfo

case class JavaJarInfo(constructor: () => api.JSparkJob[_, _],
                       className: String,
                       jarFilePath: String) extends BinaryJobInfo with SparkJobInfo {
  def job(): api.JSparkJob[_, _] = constructor.apply()
}

//For python jobs, there is no class loading or constructor required.
case class PythonJobInfo(eggPath: String) extends BinaryJobInfo with SparkJobInfo

trait JobCache {
  /**
   * Retrieves the given SparkJob class from the cache if it's there, otherwise use the DAO to retrieve it.
   * @param appName the appName under which the binary was uploaded
   * @param uploadTime the upload time for the version of the binary wanted
   * @param classPath the fully qualified name of the class/object to load
   */
  def getSparkJob(appName: String, uploadTime: DateTime, classPath: String): JobJarInfo

  /**
    * Retrieves a Python job egg location from the cache if it's there, otherwise use the DAO to retrieve it.
    * @param appName the appName under which the binary was uploaded
    * @param uploadTime the upload time for the version of the binary wanted
    * @param classPath the fully qualified name of the class/object to load
    * @return The case class containing the location of the binary file for the specified job.
    */
  def getPythonJob(appName: String, uploadTime: DateTime, classPath: String): PythonJobInfo
  def getJavaJob(appName: String, uploadTime: DateTime, classPath: String): JavaJarInfo
}