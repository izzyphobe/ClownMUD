package mud
import scala.io.StdIn
import scala.io.Source

class Room(val name:String,val description:String,val location:Int,private var items:List[Item], val exits:Array[Int]){
  def describe():String={
    println(description)
  }
  def getExit(dir:Int):Option[Room]{=
    if(exits(dir)==-1) None else Some[exits(dir)] //returns None, or the room location in given direction
  }
    def addItem(item:Item):Unit={
      ???//add to the list of items in room
    }
  }

object Room{
  val rooms=getRooms()
  def readRoom(rooms:Iterator[String]):Room={
    val location=rooms.next
    val name=rooms.next
    val description=rooms.next
    val items=List.fill(rooms.next.toInt) Item(rooms.next,rooms.next)
    val exits=rooms.next.split(",").map(_.toInt)
    new Room(name, description, location, items, exits)
  }
  def getRooms():Array[Room]={
    val map=source.fromFile("map.txt").getLines()
    val rooms=array.fill(map.next.toInt)(readRoom(map))
    source.close()
    rooms
    
    }
  }
}