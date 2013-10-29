package test.scala.job

import simulator.{Delay, Job, Queue}
import org.scalatest._

class JobSpec extends FlatSpec {
  "A Job" should "tick and decrease it's delay" in {

    val job = new Job(Queue(0), Seq[Delay](), Delay(10, 10, true), "job_name")
    val tickedJob = job.tick

    assert(tickedJob.delay.delayTimeLeft == (job.delay.delayTimeLeft -1))
  }

  it should "get delay reset if delayTime left is less than 0" in {
    val job = new Job(Queue(0), Seq[Delay](), Delay(10, 0, true), "job_name")
    val tickedJob = job.tick

    assert(tickedJob.delay.delayTimeLeft == (tickedJob.delay.delayTime))
  }

  it should "feed the queue" in {
    val job = new Job(Queue(10), Seq[Delay](), Delay(10, 0, true), "job_name")
    val fedJob = job.feed(10)

    assert(fedJob.queue.currentSize == 20)
  }

  it should "starve the queue" in {
    val job = new Job(Queue(10), Seq[Delay](), Delay(10, 0, true), "job_name")
    val fedJob = job.starve(10)

    assert(fedJob.queue.currentSize == 0)
  }

  it should "sum up the values of items to move on in a tock" in {
    val job = new Job(Queue(10), Seq[Delay](Delay(10,0, true)), Delay(0, 0, true), "job_name")
    val tockedJobSum = job.tockFeed

    assert(tockedJobSum == 1)
  }

  it should "sum up all values of items to move on in a tock if delaytime is 0" in {
    val job = new Job(Queue(10), Seq[Delay](Delay(0,0, true)), Delay(0, 0, true), "job_name")
    val tockedJobSum = job.tockFeed

    assert(tockedJobSum == 10)
  }
}