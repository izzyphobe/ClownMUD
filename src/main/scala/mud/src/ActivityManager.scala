package mud

import akka.actor.Actor
import akka.actor.ActorRef
import java.util.TimerTask
import java.util.Timer

case class Activity(time: Int, sentby: ActorRef, sentto: ActorRef, function: (ActorRef, ActorRef) => Unit)
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
          currentAct.sentto ! currentAct.function(currentAct.sentby, currentAct.sentto)
        }
      }

    case m =>
  }
  def higherP(a: Activity, b: Activity): Boolean = {
    if (a.time > b.time) true else false
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
}