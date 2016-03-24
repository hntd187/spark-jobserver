package ooyala.common.akka.metrics

import com.codahale.metrics._

/**
 * Utility trait to make metrics creation slightly less verbose
 */

object YammerMetrics {

  val metrics = new MetricRegistry()
}

trait YammerMetrics {

  def meter(name: String): Meter = YammerMetrics.metrics.meter(MetricRegistry.name(getClass, name))

  def gauge[T](name: String, metric: => T): Gauge[T] = {
    YammerMetrics.metrics.register(MetricRegistry.name(getClass, name), new Gauge[T]() {
      override def getValue: T = metric
    })
  }

  def histogram(name: String): Histogram = YammerMetrics.metrics.histogram(MetricRegistry.name(getClass, name))

  def timer(name: String): Timer = {
    YammerMetrics.metrics.timer(MetricRegistry.name(getClass, name))
  }
}
