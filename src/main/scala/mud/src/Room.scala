/*
*
* package mud
import scala.io.StdIn
import scala.io.Source

class Room(val name:String,val description:String,val location:Int,private var items:List[Item], val exits:Array[Int]){
  def describe():Unit={
    println(description)
  }
  def getExit(dir:Int):Option[Room]={
    if(exits(dir)==(-1)) None else Some(rooms(player.location).exits(dir)) //returns None, or the room location in given direction
  }
    def addItem(item:Item):Unit={
      ???//add to the list of items in room
    }
  

object Room{
  def readRoom(rooms:Iterator[String]):Room={
    val location=rooms.next
    val name=rooms.next
    val description=rooms.next
    val items=List.fill(rooms.next.toInt)(Item(rooms.next,rooms.next))
    val exits=rooms.next.split(",").map(_.toInt)
    new Room(name, description,location.toInt, items, exits)
  }
  def getRooms():Array[Room]={
    val source=Source.fromFile("map.txt")
    val map=source.getLines()
    val rooms=Array.fill(map.next.toInt)(readRoom(map))
    source.close()
    rooms
    
    }
  }
}
*/
