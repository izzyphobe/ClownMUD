package mud
import scala.io.StdIn
import scala.io.Source

class Room(val name:String,val description:String,var items:List[Item], val exits:Array[String]){
  def describe():Unit={
    println("You are in "+name+"\n"+description)
    if(items.length!=0){
      var printitems=""
      for(item<-items){
        printitems=printitems+item.name+"\n ||"+item.desc+"\n"
      }
      println("\n\n"+"You notice the following item(s):\n\n"+printitems)
    }
    else{
      println("\n\nThere are no items in the room.\n\n")
    }

    var printExits="\nYou see the following exit(s):\n\n"
    var n=0

    while(n<6){
      if(exits(n)!="x"){
        if(n==0){
          printExits=printExits+"To the North: "+Room.rooms(exits(n)).name+"\n"
        }
        else if(n==1){
          printExits=printExits+"To the South: "+Room.rooms(exits(n)).name+"\n"
        }
        else if(n==2){
          printExits=printExits+"To the East: "+Room.rooms(exits(n)).name+"\n"
        }
        else if(n==3){
          printExits=printExits+"To the West: " +Room.rooms(exits(n)).name+"\n"
        }
        else if(n==4){
          printExits=printExits+"Up: "+Room.rooms(exits(n)).name+"\n"
        }
        else if(n==5){
          printExits=printExits+"Down: "+Room.rooms(exits(n)).name+"\n"
        }
        else{
          printExits=printExits+"\n"
        }
      }
     else{
        printExits=printExits+""
      }
     n+=1
    }
    
    println(printExits)
  }

 
    def addItem(item:Item):Unit={
      items=items:+item
    }

}

object Room{
  val rooms=getRooms()
  def readRoom(rooms:Iterator[String]):(String,Room)={
    val name=rooms.next
    val description=rooms.next
    val items=List.fill(rooms.next.toInt)(Item(rooms.next,rooms.next))
    val exits=rooms.next.split(",")
    val pname=name.replace("_"," ")
    (name, new Room(pname, description, items, exits))
  }
  def getRooms():Map[String, Room]={
    val source=Source.fromFile("src/main/scala/mud/src/resources/map.txt")
    val map=source.getLines()
    val rooms=Array.fill(map.next.toInt)(readRoom(map))
    source.close()
    rooms.toMap
    
    }

}
