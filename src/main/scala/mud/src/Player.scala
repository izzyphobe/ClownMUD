package mud
import scala.io.StdIn
import akka.actor.Actor
import akka.actor.ActorRef
import java.io.BufferedReader
import java.io.PrintStream
import java.net.Socket
import java.util.Timer
import java.util.TimerTask
import scala.util.Random

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
  var helpMsg = "-------------COMMANDS------------- \n \n n, s, e, w, u, d - moves player \n  look - reprints description of current room \n"
  helpMsg += "inv - lists current inventory \n get [item] - grab item from the room and add to your inventory \n drop [item] - drops an item from your inventory and puts it in the room \n"
  helpMsg += "tell [player] [message] -  whisper message to a player \n say [message] - say something to the room \n kill [player] - engage in combat with a player \n flee - ends combat \n"
  helpMsg += "equip [item name] - equips an item in your inventory"
  def receive = {
    case TakeExit(optRoom) =>
      if (optRoom != None) {
        location ! Room.ActorLeaves(name)
        location = optRoom.get
        location ! Room.ActorEnters(name, self)
      } else out.println("There is no exit that way!")
      if (target != None) {
        target.get ! StopCombat
        target = None

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
      out.println("You stopped combat. You or your target fled!")
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
      out.println("You took " + (power / (defense / 2)) + " points of damage.\nYour health is now at " + HP + "/" + maxHP)
      target.get ! Player.GetChat(name + " takes " + power / (defense / 2) + " points of damage!")
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
      if (target == Some) out.println("You have to flee before you can move!")
      else {
        out.println("You leave the room.")
        move(cmd, sender)
      }

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
      var it = inventory.filter(_.name.toLowerCase == toDrop.toLowerCase)
      if (it.length > 0) {
        location ! Room.DropItem(it(0))
        inventory = inventory.filterNot(_.name.toLowerCase == toDrop.toLowerCase)
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
        val directions=Array("n","s","e","w")
        out.println("You try to move in a random direction!")
        location ! Room.GetExit(directions(Random.nextInt(4)))
      } else out.println("You aren't fighting anyone!")
      

    } else if (cmd.startsWith("equip")) {
      var toEquip = inventory.filterNot(_.name.toLowerCase == cmd.slice(5, cmd.length).toLowerCase)
      if (toEquip.length < 1) out.println("You don't have that item, you can't equip it!")
      else {
        if (equipped != None) out.println("You unequip your " + equipped.get.name + " and equip your " + toEquip(0).name)
        else out.println("You equip your " + toEquip(0).name)
        equipped = Some(toEquip(0))
        strength += equipped.get.strength
        defense += equipped.get.strength
        out.println("Your strength is now " + strength)
      }
    } else if (cmd.startsWith("tell")) {
      var end = cmd.split(" ").tail
      var toTell = end(0)
      var message = end.tail.mkString(" ")
      context.parent ! PlayerManager.Tell(name, toTell, message)
    } else {
      out.println("Invalid command! Type 'help' for a list of all available commands.")
    }
  }

  //STATS
  var defense = 10
  var maxHP = 100
  var target: Option[ActorRef] = None
  var equipped: Option[Item] = None
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
        t += 1
        if (t >= 3) {
          Main.activityManage ! ActivityManager.Schedule(Activity(1000, target.get, Player.TakeDamage(strength, self)))
          t = 0
        }
      }
      println(t)
    }
    run()
  }
  timer.scheduleAtFixedRate(task, 0, 1000)

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