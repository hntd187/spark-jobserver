package ooyala.common.akka

import java.util.concurrent.TimeUnit

import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricName

/**
 * ActorMetrics is a trait that provides the following metrics:
 * * message-handler.meter.{mean,m1,m5,m15} = moving avg of rate at which receive handler is called
 * * message-handler.duration.{mean,p75,p99,p999} = histogram of wrappedReceive() running time
 *
 * NOTE: the number of incoming messages can be tracked using meter.count.
 */
trait ActorMetrics extends ActorStack {
  // Timer includes a histogram of wrappedReceive() duration as well as moving avg of rate of invocation
  val className = getClass.getName.split("\\.")
  val metricName = new MetricName(className.init.mkString("."), className.last, "message-handler")
  val metricReceiveTimer = Metrics.newTimer(metricName, TimeUnit.MILLISECONDS, TimeUnit.SECONDS)

  override def receive: Receive = {
    case x =>
      val context = metricReceiveTimer.time()
      try {
        super.receive(x)
      } finally {
        context.stop()
      }
  }
}
