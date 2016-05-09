package spark.jobserver.context

import org.apache.spark.SparkConf

import com.typesafe.config.Config
import spark.jobserver.ContextLike

trait SparkContextFactory {

  type C <: ContextLike

  /**
    * Creates a SparkContext or derived context.
    *
    * @param sparkConf the Spark Context configuration.
    * @param config the context config
    * @param contextName the name of the context to start
    * @return the newly created context.
    */
  def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C

  /**
    * Creates a SparkContext or derived context.
    *
    * @param config the overall system / job server Typesafe Config
    * @param contextConfig the config specific to this particular context
    * @param contextName the name of the context to start
    * @return the newly created context.
    */
  def makeContext(config: Config, contextConfig: Config, contextName: String): C
}