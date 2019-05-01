package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import java.net.ServerSocket
import java.io.PrintStream
import java.io.BufferedReader
import java.net.Socket

class PlayerManager extends Actor {
  import PlayerManager._
  private var _children: Map[String,ActorRef] = Map()
  def receive = {
    case AddPlayer(sock, in, out) =>
      addPlayer(sock, in, out)
      println("player class reached")
    case CheckInput =>
      
      for (player <- context.children) {
        player ! Player.ProcessInput
      }

    case Check(msg) =>
      println(msg)
    case GlobalChat(msg,sender)=>
      for(child<-_children) child._2 ! Player.GetChat(sender+"  said "+msg.toString())
      
    case PlayerDone(player,name)=>
          println("player made: "+name)
          _children=_children+((name,player))
    
    case m => println("bad thingy in player manager: " + m)
  }

  var playerNum = 0
  def addPlayer(sock: Socket, in: BufferedReader, out: PrintStream) = {
    println("playermanager got it")
    playerNum += 1
    var newPlay = context.actorOf(Props(new Player(
      playerNum.toString, "", "", null, List(Item("Starter Bow", "Emblazoned on the front:'Nerf or Nothing'")), sock, in, out)), playerNum.toString)


    println("character creation done in player manager")
    Main.roomManage ! RoomManager.SetStartRoom(newPlay)
  }
//  def newChild(n:String,pl:ActorRef)=???
  def children(x: String) = _children(x)
}

object PlayerManager {
  case class AddPlayer(sock: Socket, in: BufferedReader, out: PrintStream)
  case object CheckInput
  case class GlobalChat(msg: String, sentby: String)
  case class SingleChat(msg: String, sentby: String, sentto: String)
  case class PlayerDone(player:ActorRef,name:String)
  case class Check(msg:String)

}

