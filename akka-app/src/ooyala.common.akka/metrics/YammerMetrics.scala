package ooyala.common.akka.metrics

import com.codahale.metrics._

object YammerMetrics {

  val metrics = new MetricRegistry()
}

trait YammerMetrics {

  def meter(name: String): Meter = {
    if (YammerMetrics.metrics.getNames.contains(MetricRegistry.name(getClass, name))) {
      YammerMetrics.metrics.getMeters().get(MetricRegistry.name(getClass, name))
    } else {
      YammerMetrics.metrics.meter(MetricRegistry.name(getClass, name))
    }
  }

  def gauge[T](name: String, metric: => T): Gauge[_] = {
    if (YammerMetrics.metrics.getNames.contains(MetricRegistry.name(getClass, name))) {
      YammerMetrics.metrics.getGauges().get(MetricRegistry.name(getClass, name))
    } else {
      YammerMetrics.metrics.register(MetricRegistry.name(getClass, name), new Gauge[T]() {
        override def getValue: T = metric
      })
    }
  }

  def histogram(name: String): Histogram = {
    if (YammerMetrics.metrics.getNames.contains(MetricRegistry.name(getClass, name))) {
      YammerMetrics.metrics.getHistograms().get(MetricRegistry.name(getClass, name))
    } else {
      YammerMetrics.metrics.histogram(MetricRegistry.name(getClass, name))
    }
  }

  def timer(name: String): Timer = {
    if (YammerMetrics.metrics.getTimers.containsKey(MetricRegistry.name(getClass, name))) {
      YammerMetrics.metrics.getTimers().get(MetricRegistry.name(getClass, name))
    } else {
      YammerMetrics.metrics.timer(MetricRegistry.name(getClass, name))
    }
  }
}
