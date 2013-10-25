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
   * Activates this job
   * - Activates all workers if there are enough entities in the job's queue
   * - Removes entities of the current job's queue and feeds them to appropriate workers
   * - Activates decrementing of the delay
   *
   * @return Activated Job
   */
  def activate: Job = {
    var counter = 0;
    val w = workers.map( f=>
      if (!f.munching && (f.delayTime != 0) && ((queue.currentSize - counter) > 0) && delay.delayTimeLeft == 0) {
//        counter += 1;
        f.munch
      } else {
        f
      }
    ).toSeq
    new Job(queue.remove(counter), w, delay.munch, name)
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

  override def toString():String = {
    val workerStat = workers.foldLeft("")(_+"\t" + ":" + _.delayTimeLeft)
    return "\t" + name + "\t" + delay.delayTimeLeft + "\t" + queue.currentSize+ workerStat
  }

}

object Job {
  def fromConfig(name:String, numWorkers: Int, workerProcessingTime:Int, delay: Int, queueSize:Int = 0) :Job = {
    val workers = for (i <- List.range(0, numWorkers) ) yield Delay(workerProcessingTime, workerProcessingTime, false)

    new Job(new Queue(queueSize), workers, Delay(delay, delay, false), name)
  }
}