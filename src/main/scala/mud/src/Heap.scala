package mud

import scala.reflect.ClassTag

class HeapQueue[A:ClassTag](priority:(A,A)=>Boolean){
  var arr:Array[A]=Array.fill(20)(null.asInstanceOf[A])

  var length=0
  def enqueue(add:A)={
    if(length>arr.length){
      var newArr=Array.fill(arr.length*2)(null.asInstanceOf[A])
      for(v<-0 to arr.length){
        newArr(v)=arr(v)
      }
    }
    length+=1
    var idx=length
    arr(idx)=add

    while(idx>1&&priority(arr(idx),arr(idx/2))){
      var tmp=arr(idx)
      arr(idx)=arr(idx/2)
      arr(idx/2)=tmp
    }
  }
  def dequeue:A={
    var i=1
    val toret=peek
    while(arr.length>i*2){ //while the length is greater than the index of the left child. so, while there is a child at i
      if(priority(arr(i*2),arr(i*2+1))){ //if left child at index > right child of index
        arr(i)=arr(i*2)  //current index data = left child data
        i*=2  //bubble the left child
      }
      else i=i*2+1 //bubble the right child
    }

    length=length-1
    toret
  }
  def peek=arr(1)

  def isEmpty= {
    length==0
  }


}
