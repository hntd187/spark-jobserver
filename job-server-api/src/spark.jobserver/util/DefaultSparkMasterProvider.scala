package spark.jobserver.util

import com.typesafe.config.Config

/**
  * Default Spark Master Provider always returns "spark.master" from the passed in config
  */
object DefaultSparkMasterProvider extends SparkMasterProvider {

  def getSparkMaster(config: Config): String = config.getString("spark.master")

}