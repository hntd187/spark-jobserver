package spark.jobserver

import org.joda.time.DateTime
import spark.jobserver.api.JSparkJob

trait SparkJobInfo

case class JobJarInfo(constructor: () => api.SparkJobBase,
                      className: String,
                      jarFilePath: String) extends BinaryJobInfo

case class JavaJarInfo(constructor: () => JSparkJob[_, _],
                       className: String,
                       jarFilePath: String) extends SparkJobInfo {
  def job(): JSparkJob[_, _] = constructor.apply()
}

trait JobCache {
  @throws[ClassNotFoundException]
  def getSparkJob(appName: String, uploadTime: DateTime, classPath: String): JobJarInfo
  @throws[ClassNotFoundException]
  def getJavaJob(appName: String, uploadTime: DateTime, classPath: String): JavaJarInfo
  def getPythonJob(appName: String, uploadTime: DateTime, classPath: String): PythonJobInfo
}