package mud

import scala.reflect.ClassTag

class HeapQueue[A:ClassTag](priority:(A,A)=>Boolean){
  var arr:Array[A]=Array.fill(20)(null.asInstanceOf[A])
  var bubble=1 //object moving up
  var stone=2 //object moving down, ends at empty bottom node
  var length=0
  def enqueue(add:A)={
    while(!(bubble==1||priority(arr(bubble-1),add))){
      arr(bubble)=arr(bubble/2)
      bubble-=1
      
    }
    
//    if(priority(arr(stone-1),add)){
//      arr(stone)=add
//      stone+=1
//    } else{
//      if(priority(arr((stone-1)/2),add)){
//        
//      }
//    }
  }
  def dequeue={
    
  }
  def peek=arr(1)
  
  def isEmpty= {
    stone-1==bubble
  }

    
}