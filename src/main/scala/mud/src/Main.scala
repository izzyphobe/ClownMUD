package mud
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.actor.Props
object Main extends App {
  val system = ActorSystem("MUD")
  val roomManage = system.actorOf(Props(new RoomManager), "RoomManager")
  val playerManage = system.actorOf(Props(new PlayerManager), "PlayerManager")
//  playerManage ! PlayerManager.AddPlayer(roomManage ! RoomManager.FirstRoom)
}

