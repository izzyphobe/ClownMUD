package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import scala.xml.XML

class RoomManager extends Actor {
import PlayerManager._
  import RoomManager._
	val rooms = readRooms()
	 for(room <- context.children) room ! Room.LinkExits(rooms)
  Main.activityManage ! ActivityManager.Schedule(Activity(20,Main.NPCManage,NPCManager.MakeNPC(false,rooms("Honksley_Hall"),"Chad the Clown",100,10,10)))
  
  def receive = {
	  case SetStartRoom(player:ActorRef)=>
	    setStartRoom(player)
	  case Begin=>
	   
	    println("rooms linked")
    case m => println("Ooops in RoomManager: " + m)
    
  }

  def readRooms(): Map[String, ActorRef] = {
    val source = XML.loadFile("src/main/scala/mud/src/map.xml")
    val map = for(room <-(source\\"room")) yield readRoom(room)
    val rooms=map.toMap
    println("readRooms done")
    rooms
  }
  def setStartRoom(player:ActorRef)={
    player ! Player.GiveRoom(rooms.get("Honksley_Hall").get)
    println("start room given")
  }
  
  def readRoom(room:scala.xml.Node ): (String, ActorRef) = {
    val id=room.attribute("id").get.toString
    println("room id: "+id)
    val name = room.attribute("name").get.toString
    println("room name: "+name)
    val desc = (room \ "desc").text
    println("room desc: "+desc)
    var items:List[Item] = List()
      for(item <- (room \\ "item")){
       items::=  Item((item\\"@name").toString, (item\\"@desc").toString)
      } 
   val exits= (for(exit <- (room \\ "exit")) yield{ 
     ((exit\\"@direction").text,(exit\\"@room").text)
   }).toMap

    exits.foreach(exit=>println(exit))
    id -> context.actorOf(Props(new Room(name, desc, items, exits)), id)
  }

}

object RoomManager {
  import RoomManager._
  case class SetStartRoom(player:ActorRef)
  case class PrintExits(exits:Array[String],sender:ActorRef)
  case object Begin

}