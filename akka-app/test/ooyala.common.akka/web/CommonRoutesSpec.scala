package ooyala.common.akka.web

import akka.actor.ActorSystem
import ooyala.common.akka.metrics.YammerMetrics
import org.scalatest.{FunSpec, Matchers}
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest

class CommonRoutesSpec extends FunSpec with Matchers with ScalatestRouteTest with CommonRoutes with YammerMetrics {
  def actorRefFactory: ActorSystem = system

  val metricCounter = counter("test-counter")
  val metricMeter = meter("requests")
  val metricHistogram = histogram("test-hist")
  val metricTimer = timer("test-timer")
  val metricGauge = gauge[Int]("test-gauge", 10)

  val counterMap = Map("type" -> "counter", "count" -> 0)
  val gaugeMap = Map("type" -> "gauge", "value" -> 10)

  val meterMap = Map("type" -> "meter", "units" -> "seconds", "count" -> 0, "mean" -> 0.0,
    "m1" -> 0.0, "m5" -> 0.0, "m15" -> 0.0)
  val histMap = Map("type" -> "histogram", "median" -> 0.0, "p75" -> 0.0, "p95" -> 0.0,
    "p98" -> 0.0, "p99" -> 0.0, "p999" -> 0.0)
  val timerMap = Map("type" -> "timer", "rate" -> (meterMap - "type"),
    "duration" -> (histMap ++ Map("units" -> "milliseconds") - "type"))

  describe("/metricz route") {
    /*
        it("should report all metrics") {
          Get("/metricz") ~> commonRoutes ~> check {
            status === OK

            println(responseAs[String])
            val metricsMap = JsonUtils.mapFromJson(responseAs[String])
            val classMetrics = metricsMap(getClass.getName).asInstanceOf[Map[String, Any]]

            classMetrics.keys.toSet should equal(Set("test-counter", "test-meter", "test-hist", "test-timer", "test-gauge"))
            classMetrics("test-counter") should equal(counterMap)
            classMetrics("test-meter") should equal(meterMap)
            classMetrics("test-hist") should equal(histMap)
            classMetrics("test-timer") should equal(timerMap)
          }
        }
      }


      describe("metrics serializer") {
        it("should serialize all metrics") {
          val flattenedMap = MetricsSerializer.asFlatMap()

          List("test-meter", "test-counter", "test-timer", "test-gauge", "test-hist") foreach { metricName =>
            flattenedMap.keys should contain("ooyala.common.akka.web.CommonRoutesSpec." + metricName)
          }

          flattenedMap("ooyala.common.akka.web.CommonRoutesSpec.test-meter") should equal(meterMap)
          flattenedMap("ooyala.common.akka.web.CommonRoutesSpec.test-counter") should equal(counterMap)
          flattenedMap("ooyala.common.akka.web.CommonRoutesSpec.test-hist") should equal(histMap)
          flattenedMap("ooyala.common.akka.web.CommonRoutesSpec.test-timer") should equal(timerMap)
          flattenedMap("ooyala.common.akka.web.CommonRoutesSpec.test-gauge") should equal(gaugeMap)
        }
      }
      */
  }
}
