package mud
import scala.io.StdIn
object Main{
	def main(args: Array[String]):Unit = {
		println("MUD TIME BABEY...\n")
		val player=Player.initPlayer()
		val rooms=Room.getRooms()
		var cmd=readLine
		while(cmd!="exit"){
		  player.parseCommand(cmd)
		}
	}
}

