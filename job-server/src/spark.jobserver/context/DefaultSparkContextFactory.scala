package spark.jobserver.context

import org.apache.spark.{SparkConf, SparkContext}

import com.typesafe.config.Config
import spark.jobserver.util.SparkJobUtils
import spark.jobserver.{ContextLike, SparkJob, SparkJobBase}

/**
 * The default factory creates a standard SparkContext.
 * In the future if we want to add additional methods, etc. then we can have additional factories.
 * For example a specialized SparkContext to manage RDDs in a user-defined way.
 *
 * If you create your own SparkContextFactory, please make sure it has zero constructor args.
 */
class DefaultSparkContextFactory extends SparkContextFactory {

  type C = SparkContext with ContextLike

  override def makeContext(config: Config, contextConfig: Config, contextName: String): C = {
    val sparkConf = SparkJobUtils.configToSparkConf(config, contextConfig, contextName)
    makeContext(sparkConf, contextConfig, contextName)
  }

  override def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {
    val sc = new SparkContext(sparkConf) with ContextLike {
      def sparkContext: SparkContext = this
      def isValidJob(job: SparkJobBase): Boolean = job.isInstanceOf[SparkJob]
    }
    for ((k, v) <- SparkJobUtils.getHadoopConfig(config)) sc.hadoopConfiguration.set(k, v)
    sc
  }
}
