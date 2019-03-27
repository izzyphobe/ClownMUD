package mud
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.duration._
object Main extends App {
  val system = ActorSystem("MUD")
  val roomManage = system.actorOf(Props(new RoomManager), "RoomManager")
  val playerManage = system.actorOf(Props(new PlayerManager), "PlayerManager")
  roomManage ! RoomManager.AddPlayer
  while(true){
    Thread.sleep(100)
    playerManage ! PlayerManager.CheckInput
  }
}

