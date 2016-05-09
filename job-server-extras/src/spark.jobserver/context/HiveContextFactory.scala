package spark.jobserver.context

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

import com.typesafe.config.Config
import spark.jobserver.util.SparkJobUtils
import spark.jobserver.{ContextLike, SparkHiveJob, SparkJobBase}

class HiveContextFactory extends SparkContextFactory {
  type C = HiveContext with ContextLike

  override def makeContext(config: Config, contextConfig: Config, contextName: String): C = {
    val sparkConf = SparkJobUtils.configToSparkConf(config, contextConfig, contextName)
    makeContext(sparkConf, contextConfig, contextName)
  }

  def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {
    contextFactory(sparkConf)
  }

  protected def contextFactory(conf: SparkConf): C = {
    new HiveContext(new SparkContext(conf)) with HiveContextLike
  }
}

private[jobserver] trait HiveContextLike extends ContextLike {
  def isValidJob(job: SparkJobBase): Boolean = job.isInstanceOf[SparkHiveJob]
  def stop() { this.sparkContext.stop() }
}
