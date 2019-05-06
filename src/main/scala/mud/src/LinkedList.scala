package mud

import scala.reflect.ClassTag

//mutable doubly linked list

class LinkedList[A:ClassTag]{
  case class Node[A](var data:A,var next:Node[A],var prev:Node[A])
  val end=Node(null.asInstanceOf[A],null,null)
  end.prev=end
  end.next=end
  var len=0
  
  def add(add:A)={
    var node=Node(add,end.prev,end)
    end.prev.next=node
    end.prev=node
    len+=1
  }
  def remove(i:Int):A={
    val ret:A=if(i>=0&&i<len){
      len-=1
      var tmp=end.next
      for(n<-1 to i){
        tmp=tmp.next
        
      }
      tmp.prev.next=tmp.next
      tmp.next.prev=tmp.prev
      tmp.data
      
    }else throw new IndexOutOfBoundsException("Index "+i+" is out of bounds for length "+len)
    ret
  }
  def apply(length:Int)={
    var tmp=end.next
    for(n<- 1 to length){
      tmp=tmp.next
    }
  }
  def length= len
  def update(i:Int,add:A)={
    ???
  }
}