package mud

import akka.actor.Actor
import akka.actor.ActorRef
import java.util.TimerTask
import java.util.Timer

case class Activity(private var _time: Int, sentto: ActorRef, function:Any){
  def lowerTime= _time-=1
  def time=_time
}
class ActivityManager extends Actor {
  import ActivityManager._
  val schedule = new PriorityQueue(higherP)
  var currentAct: Activity = null.asInstanceOf[Activity]
  def receive = {
    case Schedule(act: Activity) =>
      schedule.enqueue(act)
    case CheckQueue =>
      if (!schedule.isEmpty) {
        if (schedule.peek.time >= t) {
          currentAct = schedule.dequeue
          currentAct.sentto ! currentAct.function
          t=0
        }
      }
    case Test =>
      
      schedule.enqueue(Activity(100,context.parent, println("Test worked")))

    case m => println("bad thing happened: "+m+" sent by "+sender)
  }
  def higherP(a: Activity, b: Activity): Boolean = {
    if (a.time < b.time) true else false
  }

  var t = 0
  val timer = new Timer
  val task = new TimerTask {
    def run() = t += 1
  }
  timer.scheduleAtFixedRate(task, 0, 50)

}
object ActivityManager {
  case class Schedule(act: Activity)
  case object CheckQueue
  case object Test
}