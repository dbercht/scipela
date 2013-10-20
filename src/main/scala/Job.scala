package simulator;

class Job (val queue:Queue, val workers:Seq[Delay], val delay: Delay, val name:String) {
  def tick: Job = {
    new Job(queue, workers.map( f => f.decrement ), delay.decrement, name)
  }
  override def toString():String = {
    val workerStat = workers.foldLeft("")(_+"\t" + ":" + _.delayTimeLeft)
    return "\t" + name + "\t" + delay.delayTimeLeft + "\t" + queue.currentSize+ workerStat
  }

  def activate: Job = {
    var counter = 0;
    val w = workers.map( f=>
      if (!f.munching && ((queue.currentSize - counter) > 0) && f.delayTime != 0) {
        counter += 1;
        f.munch
      } else {
        f
      }
    ).toSeq
    new Job(queue.remove(counter), w, delay.munch, name)
  }

  def feed(items: Int): Job = {
    new Job(queue.add(items), workers, delay, name)
  }

  def starve(items: Int): Job = {
    new Job(queue.remove(items), workers, delay, name)
  }
}

object Job {
  def fromConfig(name:String, numWorkers: Int, workerProcessingTime:Int, delay: Int, queueSize:Int = 0) :Job = {
    val workers = for (i <- List.range(0, numWorkers) ) yield Delay(workerProcessingTime, workerProcessingTime, false)

    new Job(new Queue(queueSize), workers, Delay(delay, delay, false), name)
  }
}