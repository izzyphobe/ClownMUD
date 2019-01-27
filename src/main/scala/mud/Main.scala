package mud
import scala.io.StdIn

/**
This is a stub for the main class for your MUD.
*/
object Main {
	def main(args: Array[String]): Unit = {
		println("MUD TIME BABEY...\n")
		Room.getRooms()
		Player.initPlayer()
		println("COMMANDS: \n \n n, s, e, w, u, d - moves player \n \n look - reprints description of current room \n inv - lists current inventory \n get [item] - grab item from the room and add to your inventory")
		var cmd=readLine
		do{
		  ???
		}while(cmd!="exit")
	}
}
