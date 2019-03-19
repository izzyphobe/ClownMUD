package mud
import scala.io.StdIn
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Player(val id:Int,val name: String, val description: String, private var location: ActorRef, private var inventory: List[Item]) extends Actor{
  import Player._
  def receive={
    case PrintMessage(message:String)=>
      println(message)
    case TakeExit(optRoom:Option[ActorRef])=>
      takeExit(optRoom)
    case TakeItem(item:Item)=>
      takeItem(item)
      
  }
  def parseCommand(cmd: String): Unit = {
    println
    if (cmd == "s" || cmd == "n" || cmd == "e" || cmd == "w" || cmd == "u" || cmd == "d") {
      move(cmd)
    } else if (cmd == "look") {
      location ! Room.GetDescription
    } else if (cmd == "inv") {
      printInv
    } else if (cmd.startsWith("get")) {
      var toGet = cmd.split("t ")(1)
      location ! Room.GetItem(toGet)
    } else if (cmd.startsWith("drop")) {
      var toDrop = cmd.split("p ")(1)
      location ! Room.DropItem(toDrop)
    } else if (cmd == "help") {
      PrintMessage("COMMANDS: \n \n n, s, e, w, u, d - moves player \n \n look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory \n drop [item] - drops an item from your inventory and puts it in the room \n exit - exits the game. Your data will not be saved. It is worth nothing.")
    } else {
      PrintMessage("Invalid command! Type 'help' for a list of all available commands.")
    }
    PrintMessage("\n")
  }

  def move(d: String): Unit = {
    val dir = d.trim
    if (dir == "n") {
      location ! Room.GetExit(0)
    } else if (dir == "s") {
      location ! Room.GetExit(1)
    } else if (dir == "e") {
      location ! Room.GetExit(2)
    } else if (dir == "w") {
      location ! Room.GetExit(3)
    } else if (dir == "u") {
      location ! Room.GetExit(4)
    } else if (dir == "d") {
      location ! Room.GetExit(5)
    }
      PrintMessage("You have entered " + (location ! Room.GetName))
      PrintMessage((location ! Room.GetDescription).toString)
  }
  def printInv(): Unit = {
    if (inventory.length == 0) {
      println("You don't have anything in your inventory! Type 'get [item name]' to grab something from the room.")
    } else {
      var toPrint = ""
      for (item <- inventory) {
        toPrint = toPrint + item.name + "\n||" + item.desc + "\n\n"
      }
      println(toPrint)
    }
  }
  def takeExit(optRoom:Option[ActorRef])={
    if(optRoom==None){
      PrintMessage("There is no exit that way.")
    }
    else{
      location=optRoom.get
    }
  }
  def takeItem(item:Item)={
    val newInv:List[Item]=item::inventory
    inventory=newInv
  }
}
object Player {
  case object ProcessInput
  case class PrintMessage(message:String)
  case class TakeExit(optRoom:Option[ActorRef])
  case class TakeItem(item:Item)
//  def initPlayer(): Player = {
//    println("What's your clown name?\n")
//    val name = readLine
//    println("\nWhat's your backstory?\n")
//    val description = readLine
//    println("\nNice. I'm Chad. My clown name is Chaddington III. Good to meet you bro.\nAlright, my first class is starting soon. See ya!\n")
//    println("[CHADDINGTON III waves goodbye. He has on a Gucci eyepatch. You don't want to ask questions.]")
//    val location = "Honksley_Hall"
//    println("\nType 'look' to look around. Type 'help' to see more commands.")
//    val inventory = List(Item("Catcher's glove", "You hate baseball, but the gloves make for good clown-punching armor."))
//    val player = new Player(name, description, location, inventory)
//    player
//  }
}