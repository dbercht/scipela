package simulator;

case class Queue (val currentSize:Int) {
  def add(items: Int): Queue = {
    new Queue(this.currentSize + items)
  }

  def remove(items: Int): Queue = {
    val newSize = currentSize - items
    newSize match {
      case i if i >= 0 => Queue(i)
      case _ => Queue(0)
    }
  }
}
