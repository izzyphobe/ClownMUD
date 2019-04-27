package mud
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.duration._
import java.net.ServerSocket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.util.{Timer,TimerTask}

object Main extends App {
  val system = ActorSystem("MUD")
  val roomManage = system.actorOf(Props(new RoomManager), "RoomManager")
  val playerManage = system.actorOf(Props(new PlayerManager), "PlayerManager")
  val activityManage=system.actorOf(Props(new ActivityManager),"ActivityManager")
  system.scheduler.schedule(0.seconds, 0.1.seconds, playerManage, PlayerManager.CheckInput)
  system.scheduler.schedule(0.seconds, 0.1.seconds, activityManage, ActivityManager.CheckQueue)

  val ss = new ServerSocket(8100)

  Future {
    while (true) {
      val sock = ss.accept()
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future {
        out.println("Beginning character creation!")
        playerManage ! PlayerManager.AddPlayer(sock, in, out)
      }

    }
  }

}

