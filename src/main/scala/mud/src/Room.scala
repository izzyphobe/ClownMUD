package mud
import scala.io.StdIn
import scala.io.Source

class Room(val name:String,val description:String,val location:Int,var items:List[Item], val exits:Array[Int]){
  def describe():Unit={
    var printitems=""
    for(item<-items){
      printitems=printitems+item.name+"\n ==="+item.desc+"==="
    }
    println("You are in room:"+name+"\n"+description+"\n"+"You notice the following items:\n"+printitems)
    var printExits="\n You see the following exit(s): "
    
    for(exit<-exits){
      if(exits(exit)!=(-1)){
        printExits=printExits+Room.rooms(exit).name
        if(exit==0){
          printExits=printExits+" to the North"
        }
        else if(exit==1){
          printExits=printExits+" to the South "
        }
        else if(exit==2){
          printExits=printExits+" to the East "
        }
        else if(exit==3){
          printExits=printExits+" to the West "
        }
        else if(exit==4){
          printExits=printExits+" up"
        }
        else{
          printExits=printExits+" down"
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
    val source=Source.fromFile("map.txt")
    val map=source.getLines()
    val rooms=Array.fill(map.next.toInt)(readRoom(map))
    source.close()
    rooms
    
    }

  }
