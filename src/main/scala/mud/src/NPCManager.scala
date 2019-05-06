package mud

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import java.util.Timer
import java.util.TimerTask

case class NPC(val hostile:Boolean,var location:ActorRef,val name:String,val maxHP:Int,val strength:Int,val defense:Int) extends Actor{
  import NPC._
  def receive={
    case Player.TakeExit(optRoom: Option[ActorRef]) =>
      location = optRoom.get
      if(target!=None) target.get ! Player.StopCombat
      target=None
    case Player.StopCombat=>
      target=None
    case Player.TakeDamage(power,attacker)=>
      if(target==None) target=Some(attacker)
      HP=HP-(power/(defense/2))
      target.get ! Player.GetChat(name+" takes "+power/(defense/2)+" points of damage!")
    case Update=>
      if(target!=None) 
      if(HP<=0) location ! Room.PlayerDies(self)
    case m => println("oopsie in npc: "+m+"\nnpc name: "+name+" sent by: "+sender)
    
  }
  var HP=maxHP
  var target: Option[ActorRef] = None
    var t = 0
  val timer = new Timer
  val task = new TimerTask {
    def run() = {
      
      if (target != None) {
        t += 1
        if(t>=3){
          Main.activityManage ! ActivityManager.Schedule(Activity(1000,target.get,Player.TakeDamage(strength,self)))
          t=0
        }
      }
    }
    run()
  }
  timer.scheduleAtFixedRate(task, 0, 1000)

}

object NPC{
  case class TakeExit(optRoom:Option[ActorRef])
  case object StopCombat
  case object Update
  case class TakeDamage(power:Int,attacker:ActorRef)
}


class NPCManager extends Actor{
  var n=0
  import NPCManager._
  def receive={
    case MakeNPC(moves:Boolean,location,name,maxHP,strength,defense)=>
      
      var act=context.actorOf(Props(NPC(moves,location,name,maxHP,strength,defense)),n.toString)
      location ! Room.ActorEnters(name,act)
      n+=1
    case Update=>
      for(child<-context.children) child ! NPC.Update
    case m=> println("bad in npcmanager: "+m)
  }


  //TODO make shortest path for npc
}

object NPCManager{
  case class MakeNPC(moves:Boolean,location:ActorRef,name:String,maxHP:Int,strength:Int,defense:Int)
  case object Update
  
}