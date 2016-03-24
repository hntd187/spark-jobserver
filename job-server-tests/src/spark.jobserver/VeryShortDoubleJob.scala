package spark.jobserver

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._

/**
 * A very short job for stress tests purpose.
 * Small data. Double every value in the data.
 */
object VeryShortDoubleJob extends SparkJob {
  private val data = Array(1, 2, 3)

  def main(args: Array[String]) {
<<<<<<< a8805815585d384253ffbb1712bc2a25c0664b68
    val conf = new SparkConf().setMaster("local[4]").setAppName("VeryShortDoubleJob")
=======
    val conf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("VeryShortDoubleJob")
>>>>>>> Part of an extensive update for this...
    val sc = new SparkContext(conf)
    val config = ConfigFactory.parseString("")
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {
    SparkJobValid
  }

  override def runJob(sc: SparkContext, config: Config): Any = {
    val dd = sc.parallelize(data)
    dd.map(_ * 2).collect()
  }
}
