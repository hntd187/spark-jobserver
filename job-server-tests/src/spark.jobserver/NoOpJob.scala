package spark.jobserver

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._

/**
 * An empty no computation job for stress tests purpose.
 */
object NoOpJob extends SparkJob {
  def main(args: Array[String]) {
<<<<<<< a8805815585d384253ffbb1712bc2a25c0664b68
    val conf = new SparkConf().setMaster("local[4]").setAppName("NoOpJob")
=======
    val conf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("NoOpJob")
>>>>>>> Part of an extensive update for this...
    val sc = new SparkContext(conf)
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  def validate(sc: SparkContext, config: Config): SparkJobValidation = SparkJobValid

  def runJob(sc: SparkContext, config: Config): Any = 1
}
