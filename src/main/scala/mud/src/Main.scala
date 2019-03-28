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
import java.io.BufferedInputStream
import java.io.OutputStream
import java.io.ObjectOutputStream
import java.io.PrintStream
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {
  val sys = ActorSystem("MUD")
  val roomManage = sys.actorOf(Props(new RoomManager), "RoomManager")
  val playerManage = sys.actorOf(Props(new PlayerManager), "PlayerManager")
  
  sys.scheduler.schedule(0.seconds,0.1.seconds,playerManage,PlayerManager.CheckInput)
  val ss = new ServerSocket(8001)
  Future{
    var admin=readLine
    if(admin=="stop") {
      ss.close()
      System.exit(0)
    }
  }
  while (true) {
    val sock = ss.accept()
    val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
    val out = new PrintStream(sock.getOutputStream)
    Future {
      
      playerManage ! PlayerManager.AddPlayer(sock,in,out)
    }
    
    
  }
}

