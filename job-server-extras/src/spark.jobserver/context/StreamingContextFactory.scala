package spark.jobserver.context

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

import com.typesafe.config.Config
import spark.jobserver.util.SparkJobUtils
import spark.jobserver.{ContextLike, SparkJobBase, SparkStreamingJob}

class StreamingContextFactory extends SparkContextFactory {

  type C = StreamingContext with ContextLike

  override def makeContext(config: Config, contextConfig: Config, contextName: String): C = {
    val sparkConf = SparkJobUtils.configToSparkConf(config, contextConfig, contextName)
    makeContext(sparkConf, contextConfig, contextName)
  }

  override def makeContext(sparkConf: SparkConf, config: Config,  contextName: String): C = {
    val interval = config.getInt("streaming.batch_interval")
    val stopGracefully = config.getBoolean("streaming.stopGracefully")
    val stopSparkContext = config.getBoolean("streaming.stopSparkContext")
    new StreamingContext(sparkConf, Milliseconds(interval)) with ContextLike {
      def isValidJob(job: SparkJobBase): Boolean = job.isInstanceOf[SparkStreamingJob]
      def stop() {
        //Gracefully stops the spark context
        stop(stopSparkContext, stopGracefully)
      }
    }
  }
}
