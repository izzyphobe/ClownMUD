package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

class PlayerManager extends Actor{
  import PlayerManager._
  def receive={
    case AddPlayer=>
      addPlayer
    case m => println("bad thingy in player manager: "+m)
  }
  var playerNum=0
  private var players:List[Player]=null
  val first=Main.roomManage ! RoomManager.FirstRoom
  def addPlayer={
//    players=new Player(playerNum,"","",first,null)::players
    playerNum+=1
		println("\n\n\n\n\n\n\n\n\n\n\n\nHey, dude. Are you excited? First day of clown college!")
  }
  
}

object PlayerManager{
  case object AddPlayer
}

