package ooyala.common.akka.web

import java.util.{Map => JMap}

import com.codahale.metrics._
import ooyala.common.akka.metrics.YammerMetrics
import spray.json.JsValue
import spray.routing.HttpService

import scala.collection.JavaConversions._
import spray.json._
import spray.json.DefaultJsonProtocol._

trait CommonRoutes extends HttpService {

  val commonRoutes = {
    get {
      path("metricz") {
        // TODO: Support option to return only certain metrics classes, or turn off pretty printing
        complete {
          MetricsSerializer.serialize()
        }
      } ~ path("statusz") {
        getFromFile("statusz.html")
      }
    }
  }
}

/**
 * Serializes all the Metrics objects into JSON string
 */
object MetricsSerializer {

  // TODO: Put the class prefix thing back in...
  def serialize(registry: MetricRegistry = YammerMetrics.metrics, classPrefix: String = null): String = MetricsSerializer.toJSON(registry)

  def toJSON(registry: MetricRegistry): String = {
    val metricsMap: JMap[String, Metric] = registry.getMetrics
    val valueMap: Map[String, JsValue] = metricsMap.toMap.map {
      case (name: String, metric: Metric) => {
        metric match {
          case m: Timer     => name -> mapTimer(m)
          case m: Meter     => name -> mapMeter(m)
          case m: Gauge[_]  => name -> Map("value" -> m.getValue.asInstanceOf[Int]).toJson
          case m: Histogram => name -> mapHist(m)
        }
      }
    }
    valueMap.toJson.prettyPrint
  }

  def mapHist(h: Histogram): JsValue = {
    val snap = h.getSnapshot
    Map[String, Double](
      "median" -> snap.getMedian,
      "95th" -> snap.get95thPercentile,
      "stdDev" -> snap.getStdDev,
      "min" -> snap.getMin,
      "max" -> snap.getMax
    ).toJson
  }

  def mapMeter(m: Meter): JsValue = {
    Map[String, Double](
      "count" -> m.getCount,
      "meanRate" -> m.getMeanRate,
      "1minRate" -> m.getOneMinuteRate,
      "5minRate" -> m.getFiveMinuteRate,
      "15minRate" -> m.getFifteenMinuteRate
    ).toJson
  }

  def mapTimer(t: Timer): JsValue = {
    Map[String, Double](
      "count" -> t.getCount,
      "meanRate" -> t.getMeanRate,
      "1minRate" -> t.getOneMinuteRate,
      "5minRate" -> t.getFiveMinuteRate,
      "15minRate" -> t.getFifteenMinuteRate
    ).toJson
  }
}
