package mud.src

import akka.actor.Actor
import akka.actor.ActorRef

case class Activity(time: Int, sentby: ActorRef)
class ActivityManager extends Actor {
  import ActivityManager._
  val schedule = new PriorityQueue(higherP)
  def receive = {
    case Schedule(act: Activity) =>
      schedule.enqueue(act)
    case Update => {
      if (!schedule.isEmpty) schedule.dequeue

    }

    case m =>
  }
  def higherP(a: Activity, b: Activity): Boolean = {
    if (a.time > b.time) true else false
  }

}
object ActivityManager {
  case class Schedule(act: Activity)
  case object Update
}