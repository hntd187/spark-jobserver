package spark.jobserver

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._

import scala.util.Try

/**
 * A super-simple Spark job example that implements the SparkJob trait and can be submitted to the job server.
 *
 * Set the config with the sentence to split or count:
 * input.string = "adsfasdf asdkf  safksf a sdfa"
 *
 * validate() returns SparkJobInvalid if there is no input.string
 */
object WordCountExample extends SparkJob {
  def main(args: Array[String]) {
<<<<<<< a8805815585d384253ffbb1712bc2a25c0664b68
    val conf = new SparkConf().setMaster("local[4]").setAppName("WordCountExample")
=======
    val conf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("WordCountExample")
>>>>>>> Part of an extensive update for this...
    val sc = new SparkContext(conf)
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {
    Try(config.getString("input.string"))
      .map(x => SparkJobValid)
      .getOrElse(SparkJobInvalid("No input.string config param"))
  }

  override def runJob(sc: SparkContext, config: Config): Any = {
    sc.parallelize(config.getString("input.string").split(" ").toSeq).countByValue
  }
}
