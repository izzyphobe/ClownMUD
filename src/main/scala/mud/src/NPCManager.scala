package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

case class NPC(val hostile:Boolean,var location:ActorRef,val name:String,val maxHP:Int,val strength:Int,val defense:Int) extends Actor{
  import NPC._
  def receive={
    case TakeExit(optRoom: Option[ActorRef]) =>
      location = optRoom.get
      if(target!=None) target.get ! Player.StopCombat
    
  }
  var HP=maxHP
  var target: Option[ActorRef] = None
}

object NPC{
  case class TakeExit(optRoom:Option[ActorRef])
}


class NPCManager extends Actor{
  import NPCManager._
  def receive={
    case MakeNPC(char)=>
      context.actorOf(Props(char),char.name)
  }
  
  //TODO make NPC
  //TODO make shortest path for npc
}

object NPCManager{
  case class MakeNPC(char:NPC)
  
}