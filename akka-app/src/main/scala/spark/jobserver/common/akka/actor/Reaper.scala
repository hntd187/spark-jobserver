package spark.jobserver.common.akka.actor

import akka.actor.{ActorRef, Terminated}
import spark.jobserver.common.akka.InstrumentedActor

import scala.collection.mutable.ArrayBuffer
<<<<<<< cec1d5d76bb608f0421c158a8701d41cdd60a757:akka-app/src/main/scala/spark/jobserver/common/akka/actor/Reaper.scala
=======

import spark.jobserver.common.akka.InstrumentedActor
>>>>>>> Project Structure Updated (#626):akka-app/src/main/scala/spark/jobserver/common/akka/actor/Reaper.scala

// Taken from http://letitcrash.com/post/30165507578/shutdown-patterns-in-akka-2

object Reaper {
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
  case object Reaped
}

abstract class Reaper extends InstrumentedActor {
  import Reaper._

  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  def allSoulsReaped(): Unit

  // Watch and check for termination
  override def wrappedReceive: Receive = {
    case Reaped =>
      watched.isEmpty

    case WatchMe(ref) =>
      logger.info("Watching actor {}", ref)
      context.watch(ref)
      watched += ref

    case Terminated(ref) =>
      logger.info("Actor {} terminated", ref)
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}

class ProductionReaper extends Reaper {
  def allSoulsReaped() {
    logger.warn("Shutting down actor system because all actors have terminated")
    context.system.shutdown()
  }
}

