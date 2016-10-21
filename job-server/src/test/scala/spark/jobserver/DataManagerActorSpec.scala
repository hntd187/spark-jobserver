package spark.jobserver

<<<<<<< cec1d5d76bb608f0421c158a8701d41cdd60a757:job-server/src/test/scala/spark/jobserver/DataManagerActorSpec.scala
import java.nio.file.Files

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpecLike, Matchers}
=======
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpecLike, Matchers}
import java.nio.file.Files

import spark.jobserver.common.akka
>>>>>>> Project Structure Updated (#626):job-server/src/test/scala/spark/jobserver/DataManagerActorSpec.scala
import spark.jobserver.common.akka.AkkaTestUtils
import spark.jobserver.io.DataFileDAO

object DataManagerActorSpec {
  val system = ActorSystem("test")
}

class DataManagerActorSpec extends TestKit(DataManagerActorSpec.system) with ImplicitSender
    with FunSpecLike with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  import DataManagerActor._
  import com.typesafe.config._

  private val bytes = Array[Byte](0, 1, 2)
  private val tmpDir = Files.createTempDirectory("ut")
  private val config = ConfigFactory.empty().withValue("spark.jobserver.datadao.rootdir",
    ConfigValueFactory.fromAnyRef(tmpDir.toString))

  override def afterAll() {
    dao.shutdown()
    AkkaTestUtils.shutdownAndWait(actor)
<<<<<<< cec1d5d76bb608f0421c158a8701d41cdd60a757:job-server/src/test/scala/spark/jobserver/DataManagerActorSpec.scala
    common.akka.AkkaTestUtils.shutdownAndWait(DataManagerActorSpec.system)
=======
    akka.AkkaTestUtils.shutdownAndWait(DataManagerActorSpec.system)
>>>>>>> Project Structure Updated (#626):job-server/src/test/scala/spark/jobserver/DataManagerActorSpec.scala
    Files.delete(tmpDir.resolve(DataFileDAO.META_DATA_FILE_NAME))
    Files.delete(tmpDir)
  }

  val dao: DataFileDAO = new DataFileDAO(config)
  val actor: ActorRef = system.actorOf(Props(classOf[DataManagerActor], dao), "data-manager")

  describe("DataManagerActor") {
    it("should store, list and delete tmp data file") {
      val fileName = System.currentTimeMillis + "tmpFile"

      actor ! StoreData(fileName, bytes)
      val fn = expectMsgPF() {
        case Stored(msg) => msg
      }

      fn.contains(fileName) should be(true)
      dao.listFiles.exists(f => f.contains(fileName)) should be(true)
      actor ! DeleteData(fn)
      expectMsg(Deleted)
      dao.listFiles.exists(f => f.contains(fileName)) should be(false)
    }

    it("should list data files") {
      actor ! ListData

      val storedFiles = expectMsgPF() {
        case files => files
      }

      storedFiles should equal(dao.listFiles)
    }

    it("should store, list and delete several files") {
      val storedFiles = (for (ix <- 1 to 11; fileName = System.currentTimeMillis + "tmpFile" + ix) yield {
        actor ! StoreData(fileName, bytes)
        expectMsgPF() {
          case Stored(msg) => msg
        }
      }).toSet

      dao.listFiles should equal(storedFiles)
      storedFiles foreach (fn => {
        actor ! DeleteData(fn)
        expectMsg(Deleted)
      })
      dao.listFiles should equal(Set())
    }

  }
}
