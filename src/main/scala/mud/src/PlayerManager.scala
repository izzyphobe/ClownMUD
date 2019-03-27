package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.PoisonPill

class PlayerManager extends Actor {
  import PlayerManager._
  private var _children:List[ActorRef]=null
  def receive = {
    case AddPlayer(room: ActorRef) =>
      addPlayer(room)
    //    case GetFirst(room:Room)=>
    //      getFirst(room)
    case NewPlayer(id:String, name: String, description: String, location: ActorRef, inventory: List[Item])=>
      newPlayer(id:String, name: String, description: String, location: ActorRef, inventory: List[Item])
    case CheckInput=>
      for(player <- context.children){
        player ! Player.ProcessInput
      }
    case PrintMessage(msg:String,id:String)=>
      children(id.toInt) ! Player.PrintMessage(msg)
    case m => println("bad thingy in player manager: " + m)
  }
  var playerNum = 0
  def addPlayer(room: ActorRef) = {
    playerNum += 1
    var thisPlayer = context.actorOf(Props(new Player("", "", "", room, null)),playerNum.toString)
    thisPlayer ! Player.CreatePlayer(room,playerNum.toString)
  }
  def newPlayer(id:String, name: String, description: String, location: ActorRef, inventory: List[Item])={
    context.child(id).get ! PoisonPill
    var player = context.actorOf(Props(new Player("player"+id, name, description, location, inventory)),"player"+id.toString)
    if(_children==null){
      _children=List(player)
    }
    else{
      _children=player::_children
    }
    
  }
  //  def getFirst(room:Room):Room={
  //    val first=room
  //  }
  def children(x:Int)=_children(x)
}

object PlayerManager {
  case class AddPlayer(room: ActorRef)
  case class NewPlayer(val id:String,private var name: String, private var description: String, private var location: ActorRef, private var inventory: List[Item])
  //  case class GetFirst(room:Room)
  case object CheckInput
  case class PrintMessage(a:String,b:String)
}

