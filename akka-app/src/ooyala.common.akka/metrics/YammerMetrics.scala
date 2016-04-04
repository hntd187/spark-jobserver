package ooyala.common.akka.metrics

import com.codahale.metrics._

object YammerMetrics {

  val metrics = new MetricRegistry()
}

trait YammerMetrics {

  def meter(name: String): Meter = {
    metric[Meter](name).getOrElse {
      YammerMetrics.metrics.meter(MetricRegistry.name(getClass, name))
    }
  }

  def counter(name: String): Counter = {
    metric[Counter](name).getOrElse {
      YammerMetrics.metrics.counter(MetricRegistry.name(getClass, name))
    }
  }

  def gauge[T](name: String, metricFunc: => T): Gauge[_] = {
    metric[Gauge[T]](name).getOrElse {
      YammerMetrics.metrics.register(MetricRegistry.name(getClass, name), new Gauge[T]() {
        override def getValue: T = metricFunc
      })
    }
  }

  def histogram(name: String): Histogram = {
    metric[Histogram](name).getOrElse {
      YammerMetrics.metrics.histogram(MetricRegistry.name(getClass, name))
    }
  }

  def timer(name: String): Timer = {
    metric[Timer](name).getOrElse {
      YammerMetrics.metrics.timer(MetricRegistry.name(getClass, name))
    }
  }

  private def metric[T <: Metric](name: String): Option[T] = {
    val metricName = MetricRegistry.name(getClass, name)
    if (YammerMetrics.metrics.getNames.contains(metricName)) {
      Some(YammerMetrics.metrics.getMetrics.get(metricName).asInstanceOf[T])
    } else {
      None: Option[T]
    }
  }
}
