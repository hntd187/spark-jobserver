package ooyala.common.akka.web

import com.codahale.metrics._
import ooyala.common.akka.metrics.YammerMetrics
import spray.routing.HttpService

import scala.collection.JavaConversions._

/*
 * Defines a couple common Spray routes for metrics, status, debugging
 * * /metricz - dumps out all application metrics
 * * /statusz - dumps out GIT status of the running code
 */
trait CommonRoutes extends HttpService {

  val commonRoutes = {
    get {
      path("metricz") {
        // TODO: Support option to return only certain metrics classes, or turn off pretty printing
        complete {
          MetricsSerializer.serialize()
        }
      } ~
        path("statusz") {
          getFromFile("statusz.html")
        }
    }
  }
}

/**
 * Serializes all the Metrics objects into JSON string
 */
object MetricsSerializer {

  def serialize(registry: MetricRegistry = YammerMetrics.metrics, classPrefix: String = null): String = {
    val map = asGroupedMap(registry, classPrefix)
    JsonUtils.mapToJson(map, compact = false)
  }

  /**
   * Returns all the metrics, grouped by the class name
   *
   * @param registry    default registry if not specified
   * @param classPrefix only return metrics of this type
   * @return Map(className -> (metricName -> MetricMap))
   */
  def asGroupedMap(registry: MetricRegistry = YammerMetrics.metrics, classPrefix: String = ""): Map[String, Any] = {
    registry.getMetrics.map {
      case (name: String, counter: Counter) => counters(name, counter, classPrefix)
    }.toMap
  }

  def counters(name: String, metric: Metric, classPrefix: String): (String, Any) = {
    if (name.startsWith(classPrefix)) {
      metric match {
        case m: Counter   => classPrefix -> (name, m.getCount.toString)
        case m: Histogram => classPrefix -> (name, m.getCount)
        case m: Gauge[_]  => classPrefix -> (name, m.getValue)
        case m: Timer     => classPrefix -> (name, m.getMeanRate)
      }
    } else {
      name -> ()
    }
  }

  /*
  /** Returns all the metrics keyed by the full metric name */
  def asFlatMap(
    registry:    MetricsRegistry = Metrics.defaultRegistry(),
    classPrefix: String          = null
  ): Map[String, Map[String, Any]] = {

    // TODO: There is a fair amount of code duplication here
    val metrics = registry.allMetrics().asScala
    metrics.flatMap {
      case (metricName, metricsBlob) =>
        try {
          Some(metricName.getGroup + "." + metricName.getType + "." + metricName.getName()
            -> process(metricsBlob))
        } catch {
          case e: Exception => None
        }
    }.toMap
  }

  private def process(metric: Metric): Map[String, Any] = {
    metric match {
      case c: Counter   => Map("type" -> "counter", "count" -> c.count())
      case m: Meter     => Map("type" -> "meter") ++ meterToMap(m)
      case g: Gauge[_]  => Map("type" -> "gauge", "value" -> g.value())
      // For Timers, ignore the min/max/mean values, as they are for all time.  We're just interested
      // in the recent (biased) histogram values.
      case h: Histogram => Map("type" -> "histogram") ++ histogramToMap(h)
      case t: Timer =>
        Map("type" -> "timer", "rate" -> meterToMap(t),
          "duration" -> (histogramToMap(t) ++ Map("units" -> t.durationUnit.toString.toLowerCase)))

    }
  }

  private def meterToMap(m: Metered) =
    Map(
      "units" -> m.rateUnit.toString.toLowerCase,
      "count" -> m.count,
      "mean" -> m.meanRate,
      "m1" -> m.oneMinuteRate,
      "m5" -> m.fiveMinuteRate,
      "m15" -> m.fifteenMinuteRate
    )

  /** Extracts the histogram (Median, 75%, 95%, 98%, 99% 99.9%) values to a map */
  private def histogramToMap(h: Sampling) =
    Map(
      "median" -> h.getSnapshot().getMedian(),
      "p75" -> h.getSnapshot().get75thPercentile(),
      "p95" -> h.getSnapshot().get95thPercentile(),
      "p98" -> h.getSnapshot().get98thPercentile(),
      "p99" -> h.getSnapshot().get99thPercentile(),
      "p999" -> h.getSnapshot().get999thPercentile()
    )
  */
}
