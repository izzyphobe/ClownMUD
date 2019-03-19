package mud
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.actor.Props
object Main{
  val system=ActorSystem("MUD")
  val roomManage=system.actorOf(Props(new RoomManager),"RoomManager")
  val playerManage=system.actorOf(Props(new PlayerManager),"PlayerManager")
	def main(args: Array[String]):Unit = {
	  playerManage ! PlayerManager.AddPlayer

  }
}

