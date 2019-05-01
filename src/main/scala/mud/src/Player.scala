package mud
import scala.io.StdIn
import akka.actor.Actor
import akka.actor.ActorRef
import java.io.BufferedReader
import java.io.PrintStream
import java.net.Socket
import java.util.Timer
import java.util.TimerTask

class Player(
  val id: String,
  private var name: String,
  private var description: String,
  private var location: ActorRef,
  private var inventory: List[Item],
  val sock: Socket,
  val in: BufferedReader,
  val out: PrintStream) extends Actor {
  import Player._

  import PlayerManager._
  val helpMsg = "COMMANDS: \n \n n, s, e, w, u, d - moves player \n  look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory \n drop [item] - drops an item from your inventory and puts it in the room \n say - sends a messsage globally \n exit - exits the game. Your data will not be saved. It is worth nothing."
  def receive = {
    case TakeExit(optRoom) =>
      if (optRoom != None) {
        location = optRoom.get
        location ! Room.ActorEnters(name, self)
      } else out.println("There is no exit that way!")
      if (target != None){
        target.get ! StopCombat
         target=None

      }
     
    case TakeItem(item) =>
      if (item == None) out.println("This room does not have that item in it!")
      else {
        val newInv: List[Item] = item.get :: inventory
        inventory = newInv
      }
    case GiveRoom(room: ActorRef) =>
      location = room
      println("room given")
      createPlayer
    case ProcessInput =>
      if (in.ready()) {
        var cmd = in.readLine
        parseCommand(cmd, id)
      }
    case SendExit(name) =>
      out.println(name.toString())
    case GetDescription(desc) =>
      out.println(desc.toString())
    case GetChat(msg: String) =>
      out.println(msg.toString())
    case ChangeDescription(desc) =>
      roomdesc = desc
    case StopCombat =>
      out.println("You stopped combat.")
      target = None
    case StartAttack(toattack) =>
      if (toattack == None) out.println("That player is not in the room!")
      else if (target != None) out.println("You're already in battle!")
      else {
        target = toattack
        out.println("You engage in fisticuffs.")
      }
    case TakeDamage(power, attacker) =>
      if (0 >= HP) {
        out.println("You died of fisticuffs. I guess clown college just isn't for you, " + name + "!")
        sock.close
      }
      
      if (target == None) target = Some(attacker)
      HP = HP - (power / (defense / 2))
      out.println("You took "+(power / (defense / 2))+" points of damage.\nYour health is now at "+HP+"/"+maxHP)
    case m =>
      println("uh oh whoopsie " + m)

  }

  var roomdesc = ""
  var exits = ""
  def splitFirst(in: String, split: String) = {
    in.split(split).tail
  }
  def parseCommand(cmd: String, sender: String): Unit = {
    out.println("\n")
    if (cmd == "s" || cmd == "n" || cmd == "e" || cmd == "w" || cmd == "u" || cmd == "d") {
      move(cmd, sender)
    } else if (cmd == "look") {
      location ! Room.GetDescription(self)
      Thread.sleep(20)
      out.println(roomdesc)

    } else if (cmd == "inv") {
      printInv
    } else if (cmd.startsWith("get")) {
      var toGet = splitFirst(cmd, "t ")
      location ! Room.GetItem(toGet(0), context.self)
    } else if (cmd.startsWith("drop")) {
      var toDrop = splitFirst(cmd, ("p "))(0).toLowerCase
      var it = inventory.filter(_.name.toLowerCase == toDrop)
      if (it.length > 0) {
        location ! Room.DropItem(it(0))
        inventory = inventory.filterNot(_.name.toLowerCase == toDrop)
        Thread.sleep(20)
        out.println("You dropped your " + toDrop)
      } else out.println("You do not have that item!")
    } else if (cmd == "help") {
      out.println(helpMsg)
    } else if (cmd.startsWith("say")) {
      var toSay = cmd.slice(4, cmd.length)
      Main.playerManage ! PlayerManager.GlobalChat(toSay, name)
      //TODO single chat
    } else if (cmd.startsWith("kill")) {
      if (cmd.length >= 6) location ! Room.ScanTarget(cmd.slice(5, cmd.length))
      else out.println("Please specify a player!")
    } else if (cmd.startsWith("flee")) {
      if (target != None) {
        target.get ! StopCombat
        out.println("You stopped fighting. Coward.")
      } else out.println("You aren't fighting anyone!")
      target = None

    } else {
      out.println("Invalid command! Type 'help' for a list of all available commands.")
    }
  }

  //STATS
  var defense = 10
  var maxHP = 100
  var target: Option[ActorRef] = None
  var strength = 7
  var HP = maxHP

  def move(d: String, sender: String): Unit = {
    location ! Room.GetExit(d)
  }
  def printInv(): Unit = {
    if (inventory.length == 0) {
      out.println("You don't have anything in your inventory! Type 'get [item name]' to grab something from the room.")
    } else {
      var toPrint = ""
      for (item <- inventory) {
        toPrint = toPrint + item.name + "\n||" + item.desc + "\n\n"
      }
      out.println(toPrint)
    }
  }
  def createPlayer = {

    out.println("What's your clown name?\n")
    name = in.readLine
    out.println("What's your clown description?\n")
    description = in.readLine
    out.println(helpMsg)
    context.parent ! PlayerManager.PlayerDone(context.self, name)

  }

  var t = 0
  val timer = new Timer
  val task = new TimerTask {
    def run() = {
      if (target != None) {
        Main.activityManage ! ActivityManager.Schedule(Activity(4000, target.get, Player.TakeDamage(strength, self)))
      }
    }
    run()
  }
  timer.scheduleAtFixedRate(task, 0, 400000)

}
object Player {
  case object ProcessInput
  case class TakeExit(optRoom: Option[ActorRef])
  case class TakeItem(item: Option[Item])
  case class CreatePlayer(room: ActorRef, id: String)
  case class GiveRoom(room: ActorRef)
  case class SendExit(room: String)
  case class GetDescription(desc: String)
  case class GetChat(chat: String)
  case class ChangeDescription(desc: String)

  case class TakeDamage(attackpower: Int, from: ActorRef)
  case class StartAttack(target: Option[ActorRef])
  case object StopCombat

}