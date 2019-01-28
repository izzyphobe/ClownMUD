package mud
import scala.io.StdIn

/**
This is a stub for the main class for your MUD.
*/
object Main {
	def main(args: Array[String]): Unit = {
		println("MUD TIME BABEY...\n")
		mud.Room.getRooms()
		Player.initPlayer()
		println("COMMANDS: \n \n n, s, e, w, u, d - moves player \n \n look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory")
		var cmd=""
		do{
		  var cmd=readLine.toLowerCase
      if(cmd=="s"||cmd=="n"||cmd=="e"||cmd=="w"||cmd=="u"||cmd=="d"){
		    mud.Player.move(cmd)
		  }
		  else if(cmd=="look"){
		    mud.rooms(player.location.describe())
		  }else if(cmd=="inv"){
		    println("INVENTORY: \n"+player.inventory)
		  }else if(cmd.startsWith("get")){
		    var toGet=cmd.split("t ")(1)
		    getItem(toGet)
		  }else if(cmd.startsWith("drop")){
		    var toDrop=cmd.split("p ")(1)
		    dropItem(toDrop)
		  }else if(cmd=="help"){
		    ???
		  }
		
		  else{
		    println("Invalid command! Type 'help' for a list of all available commands.")
		  }
		}while(cmd!="exit")
	}
}
