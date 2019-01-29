package mud
import scala.io.StdIn

class Player(name:String, description:String, private var location:Int, private var inventory:List[Item]){
  def parseCommand(cmd:String):Unit={
      if(cmd=="s"||cmd=="n"||cmd=="e"||cmd=="w"||cmd=="u"||cmd=="d"){
		    move(cmd)
		  }
		  else if(cmd=="look"){
		    Room.rooms(location).describe
		  }else if(cmd=="inv"){
		    printInv
		  }else if(cmd.startsWith("get")){
		    var toGet=cmd.split("t ")(1)
		    getItem(toGet)
		  }else if(cmd.startsWith("drop")){
		    var toDrop=cmd.split("p ")(1)
		    dropItem(toDrop)
		  }else if(cmd=="help"){
		    println("COMMANDS: \n \n n, s, e, w, u, d - moves player \n \n look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory")
		  }
		
		  else{
		    println("Invalid command! Type 'help' for a list of all available commands.")
		  }

  }
  def dropItem(name:String):Unit={
    
    var newInv=inventory.filterNot(_.name==name)
    if(newInv.length==inventory.length){
      println("You don't have that item!")
    } else{
      println("You dropped your "+name+".")
      var toDrop=inventory.filter(_.name==name)
      Room.rooms(location).addItem(toDrop(0))
      inventory=newInv
    }
  }
  def getItem(name:String):Unit={
    val toGet=Room.rooms(location).items.filter(_.name==name)
    val newRoomInv=Room.rooms(location).items.filterNot(_.name==name)
    Room.rooms(location).items=newRoomInv
    inventory=inventory:+toGet(0)
  }
  def move(d:String):Unit={
    val dir=d.trim
    var toGo=(-1)
    if(dir=="n"){
      toGo=Room.rooms(location).exits(0)
    } else if(dir=="s"){
      toGo=Room.rooms(location).exits(1)
    }else if(dir=="e"){
      toGo=Room.rooms(location).exits(2)
    }else if(dir=="w"){
      toGo=Room.rooms(location).exits(3)
    }else if(dir=="u"){
      toGo=Room.rooms(location).exits(4)
    }else{
      toGo=Room.rooms(location).exits(5)
    }
    if(toGo==(-1)){
      println("There is no exit that way.")
    }
    else{
      location=toGo
      println("You have entered "+Room.rooms(location).name)
    }
  }
  def printInv():Unit={
    var toPrint=""
    for(item<-inventory){
      toPrint=toPrint+item.name+"\n     "+item.desc+"\n"
    }
    println(toPrint)
  }

}

object Player{
  def initPlayer():Player={
    println("What's your name?\n")
		val name=readLine()
		println("Describe yourself or your character.\n")
		val description=readLine
		val location=0
		val inventory=List(Item("Sharp pencil","You could probably kill someone with this if you had the stamina."),Item("Catcher's glove", "You hate baseball, but the gloves make for good armor."))
		val player=new Player(name, description, location, inventory)
    player
  }
}