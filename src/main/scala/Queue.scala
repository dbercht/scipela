package simulator;

case class Queue (val currentSize:Int) {
  def add(items: Int): Queue = {
    new Queue(this.currentSize + items)
  }

  def remove(items: Int): Queue = {
    if ((currentSize - items) < 0 ) {
      new Queue(0)
    } else {
      new Queue(this.currentSize - items)
    }
  }
}