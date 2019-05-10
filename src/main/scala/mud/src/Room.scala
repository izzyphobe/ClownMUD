package mud
import scala.io.StdIn
import scala.io.Source
import akka.actor.Actor
import akka.actor.ActorRef

case class Room(val name: String, val description: String, var items: List[Item], val exitKeys: Map[String, String]) extends Actor {
  import Room._
  import PlayerManager._
  private var exits: Map[String, ActorRef] = Map()

  def receive = {
    case LinkExits(roomsMap) =>
      for (room <- roomsMap) {
        if (exitKeys.values.toSeq.contains(room._1)) {
          exits = exits + ((room._1, room._2))
        }
      }
      println("exits linked")
    case GetName(send) =>
      println("sending message to" + send)
      context.sender ! Player.GetDescription(name)
    case GetDescription(send) =>
      send ! Player.GetChat(describe)
    case GetExit(direction) =>

      if (exitKeys.keys.toSeq.contains(direction)) {

        sender ! Player.TakeExit(Option(exits(exitKeys(direction))))
      } else {
        sender ! Player.TakeExit(None)
      }
    case ActorLeaves(name) =>
      for (i <- players) i._2 ! Player.GetChat(name + " leaves the room.")
    case GetItem(itemName, send) =>
      var it = items.find(item => item.name.toLowerCase == itemName.toLowerCase)
      send ! Player.TakeItem(it)
      if (it != None) items = items.filterNot(_ == it.get)
    case GetExitName(msg, send) =>
      send ! Player.SendExit(name)
    case DropItem(item: Item) =>
      items ::= item
    case ActorEnters(name: String, player: ActorRef) =>
      players = players + ((name, player))
      players = players.filterNot(_._1 == null)
      for (i <- players) {

        i._2 ! Player.GetChat(name + " enters the room.")
      }
    case HasPath(sender, target, dirs) =>
      if (dirs.length < 9) {
        for (exit <- exitKeys) {
          if (exit._2 == target) {
            context.parent ! RoomManager.FoundPath(sender, target, Array(exit._1 + dirs))
          } else {
            context.parent ! RoomManager.ShortestPath(sender, exit._2, target, Array(exit._1 + dirs))
          }
        }
      }

    case Chat(sender, message) =>
      println(message)
      for (player <- players) {
        if (player._2 != null) {
          player._2 ! Player.GetChat(sender + " said " + message.toString())
        }
      }
    case ScanTarget(target) =>
      var ret: Option[ActorRef] = None
      for (player <- players) {
        if (player._1.contains(target)) {
          ret = Option(player._2)
        }
      }
      sender ! Player.StartAttack(ret)
    case PlayerDies(player) =>
      var removed = players.filter(_._2 == player).keys.toList(0)
      players = players.filterNot(_._2 == player)
      for (i <- players) i._2 ! Player.GetChat(removed + " has died!")
    case m => println("no! " + m)
  }

  var players: Map[String, ActorRef] = Map((null.asInstanceOf[String], null.asInstanceOf[ActorRef]))
  val exitlist = exitnames

  def describe: String = {
    var toprint = ""
    toprint += ("You are in " + name + "\n " + description + "\n")
    if (items.length != 0) {
      var printitems = ""
      for (item <- items) {
        printitems = printitems + item.name + "\n ||" + item.desc + "\n\n"
      }
      toprint += ("\n\n" + "You notice the following item(s):\n\n" + printitems + "\n")
    } else {
      toprint += ("\n\nThere are no items in the room.\n\n")
    }

    toprint = toprint + "You see the following exits: \n" + exitlist + "\n\n"
    toprint = toprint + "The following players are in the room: \n"
    for (i <- players) toprint = toprint + i._1 + "\n"
    toprint
  }

  def exitnames: String = {
    var toprint = ""
    for (exit <- exitKeys) {
      exit._1 match {
        case "n" => toprint += "North: "
        case "s" => toprint += "South: "
        case "e" => toprint += "East: "
        case "w" => toprint += "West: "
        case "u" => toprint += "Up: "
        case "d" => toprint += "Down: "
      }
      toprint += exit._2.replace("_", " ")
      toprint += ("\n")

    }

    toprint
  }

}

object Room {
  case class LinkExits(roomsMap: Map[String, ActorRef])
  case class GetDescription(sender: ActorRef)
  case class GetExit(dir: String)
  case class GetItem(itemname: String, sender: ActorRef)
  case class DropItem(item: Item)
  case class GetName(sender: ActorRef)
  case class GetExitName(msg: String, sender: ActorRef)
  case class ActorEnters(name: String, player: ActorRef)
  case class Chat(sender: String, message: String)
  case class ScanTarget(target: String)
  case class PlayerDies(player: ActorRef)
  case class ActorLeaves(name: String)
  case class HasPath(sender: ActorRef, target: String, path: Array[String])

}
