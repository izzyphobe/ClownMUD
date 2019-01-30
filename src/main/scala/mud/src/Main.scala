package mud
import scala.io.StdIn
object Main{
	def main(args: Array[String]):Unit = {
		println("\n\n\n\n\n\n\n\n\n\n\n\nHey, dude. Are you excited? First day of clown college!")
    val player=Player.initPlayer()
		var cmd=readLine
		while(cmd!="exit"){
		  player.parseCommand(cmd.toLowerCase)
		  cmd=readLine
		}
		if(cmd.toLowerCase=="exit"){
		  System.exit(0)
		}
	}
}

