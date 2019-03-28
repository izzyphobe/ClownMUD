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
    case GetName(send)=>
      println("sending message to" +send)
      context.sender ! Player.PrintMessage(name)
    case GetDescription(send) =>
      describe(send)
    case GetExit(dir,send) =>
      send ! Player.TakeExit(getExit(dir))
    case GetItem(itemName,send) =>
      var it=getItem(itemName)
      if(it!=None){
        send ! Player.TakeItem(getItem(itemName).get)
      }

    case GetExitName(msg,send)=>
      getExitName(msg,send)
    case DropItem(item:Item) =>
      dropItem(item)
    case m=> println("no! "+m)
    
  }
  
  def dropItem(item:Item)={
    items::=item
  }
  def getItem(itemName:String):Option[Item]={
    var it=items.find(item=>item.name.toLowerCase==itemName.toLowerCase)
    if(it!=None){
      it
    }else None
  }
  def getExit(dir:Int):Option[ActorRef]={
    exits(dir)
  }
  
  def getExitName(msg:String,sent:ActorRef)={
    sent ! Player.PrintMessage(name)
  }
  
  def describe(send:ActorRef)={
    var toprint=""
    toprint+=("You are in "+name+"\n"+description)
    if(items.length!=0){
      var printitems=""
      for(item<-items){
        printitems=printitems+item.name+"\n ||"+item.desc+"\n"
      }
      toprint+=("\n\n"+"You notice the following item(s):\n\n"+printitems)
    }
    else{
      toprint+=("\n\nThere are no items in the room.\n\n")
    }

    toprint+="\nYou see the following exit(s):\n\n"
    var n=0
    send ! Player.PrintMessage(toprint)
    toprint=""
    context.parent ! RoomManager.PrintExits(exitKeys,send)
    
    
    toprint
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
  case class GetDescription(sender:ActorRef)
  case class GetExit(dir:Int,sender:ActorRef)
  case class GetItem(itemname:String,sender:ActorRef)
  case class DropItem(item: Item)
  case class GetName(sender:ActorRef)
  case class GetExitName(msg:String,sender:ActorRef)

}
