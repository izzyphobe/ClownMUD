package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props


class RoomManager extends Actor {
import PlayerManager._
  import RoomManager._
	val rooms = readRooms()
	for(room <- context.children) room ! Room.LinkExits(rooms)

  def receive = {
	  case SetStartRoom(player:ActorRef)=>
	    setStartRoom(player)
    case PrintExits(exits, sender)=>
      printExits(exits,sender)
    case m => println("Ooops in RoomManager: " + m)
  }
  def printExits(exits:Array[String],sender:ActorRef)={
    var n=0
    var toprint=""
    while(n<6){
      if((exits(n)!="x")&&rooms(exits(n))!=None){
        if(n==0){
          rooms(exits(n)) ! Room.GetExitName("north: ",sender)

        }
        else if(n==1){
          rooms(exits(n)) ! Room.GetExitName("south: ",sender)

        }
        else if(n==2){
          rooms(exits(n)) ! Room.GetExitName("east: ",sender)

        }
        else if(n==3){
          rooms(exits(n)) ! Room.GetExitName("west: ",sender)

        }
        else if(n==4){
          rooms(exits(n)) ! Room.GetExitName("up: ",sender)

        }
        else if(n==5){
          rooms(exits(n)) ! Room.GetExitName("down: ",sender)

        }
      }
     n+=1
    }
  }
  def readRooms(): Map[String, ActorRef] = {
    val source = scala.io.Source.fromFile("src/main/scala/mud/bin/resources/map.txt")
    val lines = source.getLines()
    val rooms = Array.fill(lines.next.trim.toInt)(readRoom(lines)).toMap
    source.close()
    rooms
  }
  def setStartRoom(player:ActorRef)={
    player ! Player.GiveRoom(rooms("Honksley_Hall"))
    println("start room given")
  }
  
  def readRoom(lines: Iterator[String]): (String, ActorRef) = {
    val keyword = lines.next
    val name = lines.next
    val desc = lines.next
    val items = List.fill(lines.next.trim.toInt) {
      Item(lines.next, lines.next)
    }
    val exits = lines.next.split(",").map(_.trim)
    keyword -> context.actorOf(Props(new Room(name, desc, items, exits)), keyword)
  }

}

object RoomManager {
  import RoomManager._
  case class SetStartRoom(player:ActorRef)
  case class PrintExits(exits:Array[String],sender:ActorRef)

}