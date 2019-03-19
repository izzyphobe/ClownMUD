package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props


class PlayerManager extends Actor{
  import PlayerManager._
  def receive={
    case AddPlayer(room:ActorRef)=>
      addPlayer(room)
//    case GetFirst(room:Room)=>
//      getFirst(room)
    case m => println("bad thingy in player manager: "+m)
  }
  var playerNum=0
  private var players:List[Player]=null
  def addPlayer(room:ActorRef)={
    players=new Player(playerNum,"","",room,null)::players
    playerNum+=1
		println("\n\n\n\n\n\n\n\n\n\n\n\nHey, dude. Are you excited? First day of clown college!")
  }
//  def getFirst(room:Room):Room={
//    val first=room
//  }
}

object PlayerManager{
  case class AddPlayer(room:ActorRef)
//  case class GetFirst(room:Room)
}

