package mud

class Player(name:String, description:String, private var location:Int, private var inventory:List[Item]){
  def processCommand(cmd:String)={
    ???
  }
  def remItem(name:String):Unit={
    
    var newInv=inventory.filterNot(_.name==name)
    if(newInv.length==inventory.length){
      println("You don't have that item!")
    } else{
      println("You dropped your "+name+".")
      Room.addItem(name)
      ???//add to list of items in room
      inventory=newInv
    }
  }
  def getItem(name:String):Unit={
    ???
  }
  def move(dir:String):Unit={
    ???
  }
}

object Player{
  def initPlayer():Player={
    println("What's your name?\m")
		val name=readLine
		println("Describe yourself or your character.\n")
		val description=readLine
		val location=0
		val inventory=List(Item("Sharp pencil","You could probably kill someone with this if you had the stamina."),Item("Catcher's glove", "You hate baseball, but the gloves make for good armor."))
		val player=new Player(name, description, location, inventory)
  }
}