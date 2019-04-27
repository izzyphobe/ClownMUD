package mud
import scala.io.StdIn
import scala.io.Source
import akka.actor.Actor
import akka.actor.ActorRef

case class Room(val name: String, val description: String, var items: List[Item], val exitKeys: Array[String]) extends Actor {
  import Room._
  import PlayerManager._
  private var exits: Array[Option[ActorRef]] = null

  def receive = {
    case LinkExits(roomsMap) =>
      exits = exitKeys.map(keyword => roomsMap.get(keyword))
    case GetName(send) =>
      println("sending message to" + send)
      context.sender ! Player.GetDescription(name)
    case GetDescription(send) =>
      send ! Player.GetChat(describe)
    case GetExit(dir, send) =>
      send ! Player.TakeExit(exits(dir))
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
      players=players.filterNot(_._1==null)
      for (i <- players) {

        i._2 ! Player.GetChat(name + " enters the room.")
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
        if (player._1 == target) {
          ret = Option(player._2)
        }
      }
      sender ! Player.StartAttack(ret)
    case m => println("no! " + m)
  }

  var players: Map[String, ActorRef] = Map((null.asInstanceOf[String], null.asInstanceOf[ActorRef]))
  val exitlist=exitnames
  

  def describe: String = {
    var toprint = ""
    toprint += ("You are in " + name + "\n " + description + "\n")
    if (items.length != 0) {
      var printitems = ""
      for (item <- items) {
        printitems = printitems + item.name + "\n ||" + item.desc + "\n"
      }
      toprint += ("\n\n" + "You notice the following item(s):\n\n" + printitems + "\n")
    } else {
      toprint += ("\n\nThere are no items in the room.\n\n")
    }

    toprint+"You see the following exits: \n"+exitlist
  }

  def exitnames: String = {
    var toprint = ""
    var n = 0
    for (exit <- exitKeys) {
      if (exit != "x") {
        if (n == 0) toprint += "North: " + exit.replace("_", " ")
        else if (n == 1) toprint += "South: " + exit.replace("_", " ")
        else if (n == 2) toprint += "East: " + exit.replace("_", " ")
        else if (n == 3) toprint += "West: " + exit.replace("_", " ")
        else if (n == 4) toprint += "Up: " + exit.replace("_", " ")
        else if (n == 5) toprint += "Down: " + exit.replace("_", " ")
        toprint+=("\n")
      }
      n += 1
    }

    toprint
  }

}

object Room {
  case class LinkExits(roomsMap: Map[String, ActorRef])
  case class GetDescription(sender: ActorRef)
  case class GetExit(dir: Int, sender: ActorRef)
  case class GetItem(itemname: String, sender: ActorRef)
  case class DropItem(item: Item)
  case class GetName(sender: ActorRef)
  case class GetExitName(msg: String, sender: ActorRef)
  case class ActorEnters(name: String, player: ActorRef)
  case class Chat(sender: String, message: String)
  case class ScanTarget(target: String)

}
