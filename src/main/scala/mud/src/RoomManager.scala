package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

class RoomManager extends Actor {
  import RoomManager._
	val rooms = readRooms()
	for(room <- context.children) room ! Room.LinkExits(rooms)

  def receive = {
	  case FirstRoom=>
	    sender ! rooms("Honksley_Hall")
    case m => println("Ooops in RoomManager: " + m)
  }

  def readRooms(): Map[String, ActorRef] = {
    val source = scala.io.Source.fromFile("src/main/scala/mud/bin/resources/map.txt")
    val lines = source.getLines()
    val rooms = Array.fill(lines.next.trim.toInt)(readRoom(lines)).toMap
    source.close()
    rooms
  }
  def firstRoom=rooms("Honksley_Hall")
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
  case object FirstRoom
}