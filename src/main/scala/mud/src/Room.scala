package mud
import scala.io.StdIn
import scala.io.Source
import akka.actor.Actor
import akka.actor.ActorRef

class Room(val name:String,val description:String,var items:List[Item], val exitKeys:Array[String]) extends Actor{
  import Room._
  private var exits:Array[Option[ActorRef]]=null
  def receive={
    case LinkExits(roomsMap)=>
      exits=exitKeys.map(keyword=>roomsMap.get(keyword))
    case GetName=>
      sender ! name
    case GetDescription =>
      sender ! Player.PrintMessage(description)
    case GetExit(dir) =>
      sender ! Player.TakeExit(getExit(dir))
    case GetItem(itemName) =>
      sender ! Player.TakeItem(getItem(itemName))
    case DropItem(item:String) =>
      dropItem(item)

    case m=> println("no! "+m)
    
  }
  
  def dropItem(item:String)={
    items.filter(_.name==item)
  }
  def getItem(itemName:String):Item={
    items.find(item=>item.name==itemName).get
  }
  def getExit(dir:Int):Option[ActorRef]={
    exits(dir)
  }
  
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
      if(exitKeys(n)!="x"){
        if(n==0){
          printExits=printExits+"To the North: "+ (exits(n).get ! GetName)+"\n"
        }
        else if(n==1){
          printExits=printExits+"To the South: "+(exits(n).get ! GetName)+"\n"
        }
        else if(n==2){
          printExits=printExits+"To the East: "+(exits(n).get ! GetName)+"\n"
        }
        else if(n==3){
          printExits=printExits+"To the West: " +(exits(n).get ! GetName)+"\n"
        }
        else if(n==4){
          printExits=printExits+"Up: "+(exits(n).get ! GetName)+"\n"
        }
        else if(n==5){
          printExits=printExits+"Down: "+(exits(n).get ! GetName)+"\n"
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
    
    sender ! Player.PrintMessage(printExits)
  }

 
    def addItem(item:Item):Unit={
      items=items:+item
    }

}

object Room{
//  val rooms=getRooms()
//  def readRoom(rooms:Iterator[String]):(String,Room)={
//    val name=rooms.next
//    val description=rooms.next
//    val items=List.fill(rooms.next.toInt)(Item(rooms.next,rooms.next))
//    val exits=rooms.next.split(",")
//    val pname=name.replace("_"," ")
//    (name, new Room(pname, description, items, exits))
//  }
//  def getRooms():Map[String, Room]={
//    val source=Source.fromFile("src/main/scala/mud/src/resources/map.txt")
//    val map=source.getLines()
//    val rooms=Array.fill(map.next.toInt)(readRoom(map))
//    source.close()
//    rooms.toMap
//    
//    }
  case class LinkExits(roomsMap:Map[String,ActorRef])
  case object GetDescription
  case class GetExit(dir:Int)
  case class GetItem(itemname:String)
  case class DropItem(item: String)
  case object GetName

}
