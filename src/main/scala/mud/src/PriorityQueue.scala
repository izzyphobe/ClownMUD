package mud

import akka.actor.Actor
import akka.actor.ActorRef
import scala.reflect.ClassTag

class PriorityQueue[A](higherP: (A, A) => Boolean) { 
  private var default:A = _
  case class Node(val data: A, var prev: Node, var next: Node)  
  private val t =  Node(default, null, null)
  t.prev = t
  t.next = t
  
  def enqueue(elem: A):Unit = {
    val newNode =  Node(elem, t.prev, t)
    t.prev.next = newNode
    t.prev = newNode

  }
  
  def dequeue: A = {
    val torem = findHighestPriority
    torem.prev.next = torem.next
    torem.next.prev = torem.prev
    torem.data
  }
  def peek: A = {
    findHighestPriority.data
  }
  
  def isEmpty: Boolean = t.prev == t
  
  private def findHighestPriority(): Node = {
    var ret = t.next
    var rover = ret.next
    while(rover != t){
      if(higherP(rover.data, ret.data)) ret = rover
      rover = rover.next
    }
    ret
  }
}