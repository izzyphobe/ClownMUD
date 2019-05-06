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
import java.util.{ Timer, TimerTask }

object Main extends App {
  val system = ActorSystem("MUD")
  val roomManage = system.actorOf(Props(new RoomManager), "RoomManager")
  val playerManage = system.actorOf(Props(new PlayerManager), "PlayerManager")
  val NPCManage = system.actorOf(Props(new NPCManager), "NPCManager")
  val activityManage = system.actorOf(Props(new ActivityManager), "ActivityManager")
  system.scheduler.schedule(0.seconds, 0.1.seconds, playerManage, PlayerManager.CheckInput)
  system.scheduler.schedule(0.seconds, 10.milliseconds, activityManage, ActivityManager.CheckQueue)
  system.scheduler.schedule(0.seconds, 10.milliseconds, NPCManage, NPCManager.Update)

  val ss = new ServerSocket(8086)
  activityManage ! ActivityManager.Test
  activityManage ! ActivityManager.Schedule(Activity(3000, playerManage, PlayerManager.Check("3000")))
  activityManage ! ActivityManager.Schedule(Activity(300000, playerManage, PlayerManager.Check("300000")))
  activityManage ! ActivityManager.Schedule(Activity(300, playerManage, PlayerManager.Check("300")))
  activityManage ! ActivityManager.Schedule(Activity(10, playerManage, PlayerManager.Check("10")))

  Future {
    while (true) {
      val sock = ss.accept()
      val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
      val out = new PrintStream(sock.getOutputStream)
      Future {
        out.println("Beginning character creation!")
        Thread.sleep(50)
        playerManage ! PlayerManager.AddPlayer(sock, in, out)
      }

    }
  }

}

