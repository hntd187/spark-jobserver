package ooyala.common.akka

import ooyala.common.akka.metrics.YammerMetrics

/**
 * ActorMetrics is a trait that provides the following metrics:
 * * message-handler.meter.{mean,m1,m5,m15} = moving avg of rate at which receive handler is called
 * * message-handler.duration.{mean,p75,p99,p999} = histogram of wrappedReeive() running time
 *
 * NOTE: the number of incoming messages can be tracked using meter.count.
 */
trait ActorMetrics extends ActorStack with YammerMetrics {
  // Timer includes a histogram of wrappedReceive() duration as well as moving avg of rate of invocation
  val metricReceiveTimer = timer("message-receiver")

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
