package simulator;

class Job (val queue:Queue, val workers:Seq[Delay], val delay: Delay, val name:String) {
  /**
   * Returns the ticked version of this Job
   * - Decrements every single individual worker.
   * - Decrements the delay
   *
   * @return Job Ticked Job
   */
  def tick: Job = {
    new Job(queue, workers.map( f => f.decrement ), delay.decrement, name)
  }

  /**
   * Returns the amount of queue items to
   * @return Int the total amount of items to move on to the next step
   */
  def tockFeed: Int = {
    workers.foldLeft(0)((sum,worker) =>
      worker match {
        case Delay(0, _, _) => queue.currentSize
        case Delay(_, 0, _) => sum + 1
        case _ => sum
      }
    )
  }

  /**
   * Activates this job
   * - Activates all workers if there are enough entities in the job's queue
   * - Removes entities of the current job's queue and feeds them to appropriate workers
   * - Activates decrementing of the delay
   *
   * @return Activated Job
   */
  def activate: Job = {
    var count = activeWorkers;
    val w = workers.map( f=>
      if (!f.munching && (f.delayTime != 0) && (queue.currentSize > count) && delay.delayTimeLeft == 0) {
        count += 1
        f.munch
      } else {
        f
      }
    ).toSeq
    new Job(queue, w, delay.munch, name)
  }

  /**
   * Feeds items into the queue of the job
   *
   * @param Int items Number of items to add
   * @return Job with incremented internal queue
   */
  def feed(items: Int): Job = {
    new Job(queue.add(items), workers, delay, name)
  }

  /**
   * Starves the Job's queue
   * @param Int items the number of items to remove from the queue
   *
   * @return Job with decremented internal queue
   */
  def starve(items: Int): Job = {
    new Job(queue.remove(items), workers, delay, name)
  }

  /**
   * Calculates number of current active workers
   *
   * @return Int
   */
  def activeWorkers: Int = {
    workers.foldLeft(0)((cSum, worker) =>
      worker match {
        case Delay(_, _, true) => cSum + 1
        case _ => cSum
      }
    )
  }

  override def toString():String = {
    val workerStat = workers.foldLeft("")(_+"\t" + ":" + _.delayTimeLeft)
    return "\t" + name + "\t" + delay.delayTimeLeft + "\t" + queue.currentSize+ workerStat
  }

}

object Job {
  def fromConfig(name:String, numWorkers: Int, workerProcessingTime:Int, delay: Int, delayTimeOffset: Int = 0, queueSize:Int = 0) :Job = {
    val workers = for (i <- List.range(0, numWorkers) ) yield Delay(workerProcessingTime, workerProcessingTime, false)
    new Job(new Queue(queueSize), workers, Delay(delay, delayTimeOffset, true), name)
  }
}
