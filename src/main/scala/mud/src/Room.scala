package mud
import scala.io.StdIn
import scala.io.Source
import akka.actor.Actor
import akka.actor.ActorRef

class Room(val name:String,val description:String,var items:List[Item], val exitKeys:Array[String]) extends Actor{
  import Room._
  private var exits:Array[Option[ActorRef]]=null
  private var in=""
  def receive={
    case LinkExits(roomsMap)=>
      exits=exitKeys.map(keyword=>roomsMap.get(keyword))
    case GetName(id)=>
      Main.playerManage ! PlayerManager.PrintMessage(name,id)
    case GetDescription(id) =>
      sender ! PlayerManager.PrintMessage(describe(),id)
    case GetExit(dir) =>
      sender ! Player.TakeExit(getExit(dir))
    case GetItem(itemName) =>
      sender ! Player.TakeItem(getItem(itemName))
    case DropItem(item:Item) =>
      dropItem(item)
    case In(message:String)=>
      in=message
    case GetNameFromRoom=>
      in=name

    case m=> println("no! "+m)
    
  }
  
  def dropItem(item:Item)={
    items::=item
  }
  def getItem(itemName:String):Item={
    items.find(item=>item.name==itemName).get
  }
  def getExit(dir:Int):Option[ActorRef]={
    exits(dir)
  }
  
  def describe():String={
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

    while(n<6){
      if(exits(n)==None) toprint+="\n"
      else if(exitKeys(n)!="x"){
        if(n==0){
          exits(n).get ! GetNameFromRoom
          toprint+="To the North: "+ (in)+"\n"
        }
        else if(n==1){
          exits(n).get ! GetNameFromRoom
          toprint+="To the South: "+(in)+"\n"
        }
        else if(n==2){
          exits(n).get ! GetNameFromRoom
          toprint+="To the East: "+(in)+"\n"
        }
        else if(n==3){
          exits(n).get ! GetNameFromRoom
          toprint+="To the West: " +(in)+"\n"
        }
        else if(n==4){
          exits(n).get ! GetNameFromRoom
          toprint+="Up: "+(in)+"\n"
        }
        else if(n==5){
          exits(n).get ! GetNameFromRoom
          toprint+="Down: "+(in)+"\n"
        }
        else{
          toprint+="\n"
        }
      }
     n+=1
    }
    
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
  case class GetDescription(msg:String)
  case class GetExit(dir:Int)
  case class GetItem(itemname:String)
  case class DropItem(item: Item)
  case class In(message:String)
  case class GetName(id:String)
  case object GetNameFromRoom

}
