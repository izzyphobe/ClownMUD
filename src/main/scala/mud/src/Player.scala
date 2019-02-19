package mud
import scala.io.StdIn

class Player(name: String, description: String, private var location: String, private var inventory: List[Item]) {
  def parseCommand(cmd: String): Unit = {
    println
    if (cmd == "s" || cmd == "n" || cmd == "e" || cmd == "w" || cmd == "u" || cmd == "d") {
      move(cmd)
    } else if (cmd == "look") {
      Room.rooms(location).describe
    } else if (cmd == "inv") {
      printInv
    } else if (cmd.startsWith("get")) {
      var toGet = cmd.split("t ")(1)
      getItem(toGet)
    } else if (cmd.startsWith("drop")) {
      var toDrop = cmd.split("p ")(1)
      dropItem(toDrop)
    } else if (cmd == "help") {
      println("COMMANDS: \n \n n, s, e, w, u, d - moves player \n \n look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory \n drop [item] - drops an item from your inventory and puts it in the room \n exit - exits the game. Your data will not be saved. It is worth nothing.")
    } else {
      println("Invalid command! Type 'help' for a list of all available commands.")
    }
    println
  }
  def dropItem(name: String): Unit = {
    var newInv = inventory.filterNot(_.name.toLowerCase == name.toLowerCase)
    if (newInv.length == inventory.length) {
      println("You don't have that item!")
    } else {
      println("You dropped your " + name + ".")
      var toDrop = inventory.filter(_.name.toLowerCase == name.toLowerCase)
      Room.rooms(location).addItem(toDrop(0))
      inventory = newInv

    }
  }
  def getItem(name: String): Unit = {
    if (Room.rooms(location).items.length == 0) {
      println("There aren't any items in this room.")
    } else {
      val toGet = Room.rooms(location).items.filter(_.name.toLowerCase == name.toLowerCase)
      if (toGet.length == 0) {
        println("That item isn't in thie room.")
      } else {
        val newRoomInv = Room.rooms(location).items.filterNot(_.name.toLowerCase == name.toLowerCase)
        Room.rooms(location).items = newRoomInv
        inventory = inventory :+ toGet(0)
        println("You grabbed: " + toGet(0).name)
      }
    }
  }
  def move(d: String): Unit = {
    val dir = d.trim
    var toGo = ("x")
    if (dir == "n") {
      toGo = Room.rooms(location).exits(0)
    } else if (dir == "s") {
      toGo = Room.rooms(location).exits(1)
    } else if (dir == "e") {
      toGo = Room.rooms(location).exits(2)
    } else if (dir == "w") {
      toGo = Room.rooms(location).exits(3)
    } else if (dir == "u") {
      toGo = Room.rooms(location).exits(4)
    } else if (dir == "d") {
      toGo = Room.rooms(location).exits(5)
    }
    if (toGo == "x") {
      println("There is no exit that way.")
    } else {
      location = toGo
      println("You have entered " + Room.rooms(location).name)
      Room.rooms(location).describe()
    }
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
}
object Player {
  def initPlayer(): Player = {
    println("What's your clown name?\n")
    val name = readLine
    println("\nWhat's your backstory?\n")
    val description = readLine
    println("\nNice. I'm Chad. My clown name is Chaddington III. Good to meet you bro.\nAlright, my first class is starting soon. See ya!\n")
    println("[CHADDINGTON III waves goodbye. He has on a Gucci eyepatch. You don't want to ask questions.]")
    val location = "Honksley_Hall"
    println("\nType 'look' to look around. Type 'help' to see more commands.")
    val inventory = List(Item("Catcher's glove", "You hate baseball, but the gloves make for good clown-punching armor."))
    val player = new Player(name, description, location, inventory)
    player
  }
}