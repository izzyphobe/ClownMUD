package mud
import scala.io.StdIn
import scala.io.Source

class Room(val name:String,val description:String,val location:Int,var items:List[Item], val exits:Array[Int]){
  def describe():Unit={
    var printitems=""
    for(item<-items){
      printitems=printitems+item.name+"\n ||"+item.desc+"\n"
    }
    println("You are in room:"+name+"\n"+description+"\n\n"+"You notice the following items:\n\n"+printitems)
    var printExits="\nYou see the following exit(s):\n\n"
    
    for(n<-exits){
      var m=n-1
      if(m!=99){
        if(m==0){
          printExits=printExits+"To the North: "+Room.rooms(n).name
        }
        else if(m==1){
          printExits=printExits+"To the South: "+Room.rooms(n).name
        }
        else if(m==2){
          printExits=printExits+"To the East: "+Room.rooms(n).name
        }
        else if(m==3){
          printExits=printExits+"To the West:" +Room.rooms(n).name
        }
        else if(m==4){
          printExits=printExits+"Up: "+Room.rooms(n).name
        }
        else{
          printExits=printExits+"Down:"+Room.rooms(n).name
        }
        printExits=printExits+"\n"
      }
      printExits
    }
    
    println(printExits)
  }

 
    def addItem(item:Item):Unit={
      items=items:+item
    }

}

object Room{
  val rooms=getRooms()
  def readRoom(rooms:Iterator[String]):Room={
    val location=rooms.next
    val name=rooms.next
    val description=rooms.next
    val items=List.fill(rooms.next.toInt)(Item(rooms.next,rooms.next))
    val exits=rooms.next.split(",").map(_.toInt)
    new Room(name, description,location.toInt, items, exits)
  }
  def getRooms():Array[Room]={
    val source=Source.fromFile("src/main/scala/mud/src/resources/map.txt")
    val map=source.getLines()
    val rooms=Array.fill(map.next.toInt)(readRoom(map))
    source.close()
    rooms
    
    }

  }
