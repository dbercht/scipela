package test.scala.queue

import simulator.Queue
import org.scalatest._

class QueueSpec extends FlatSpec {

  "A queue" should "receive new values" in {
    val queue = new Queue(4)
    val incrementedQueue = queue.add(5);
    assert(incrementedQueue.currentSize == 9)
  }

  It should "remove values" in {
    val queue = new Queue(6)
    val incrementedQueue = queue.remove(3);
    assert(incrementedQueue.currentSize == 3)
  }
}