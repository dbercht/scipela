package simulator.entities

object Config {
  val jenkinsDelay = 5
  val jenkinsWorkerProcessingTime = 0
  val jenkinsWorkers = Seq(Delay(Config.jenkinsWorkerProcessingTime, Config.jenkinsWorkerProcessingTime, false))
  
  val assignSupplierProcessingTime = 3
  val fraudProcessingTime = 3
}

case class Delay (val delayTime:Int, val delayTimeLeft:Int, val munching:Boolean) {
  def decrement: Delay = {
    if (!munching) return Delay(delayTime, 0, false)
    if (delayTimeLeft == 0) {
      Delay(delayTime, delayTime, false)
    } else {
      Delay(delayTime, delayTimeLeft - 1, true)
    }
  }
}

class StormDelay extends Delay(0, 0, false)
case class Queue (val currentSize:Int)

class Job (val queue:Queue, val workers:Seq[Delay], val delay: Delay, val name:String) {
  def tick: Job = {
    new Job(queue, workers.map( f => f.decrement ), delay.decrement, name)
  }
  override def toString():String = {
    val workerStat = workers.foldLeft("\nTime Left:\n")(_+"\t" + _.delayTimeLeft + "\n")
    return "\nName:\t" + name + "\nDelay Time Left:\t" + delay.delayTimeLeft + "\nQueue Length:\t" + queue.currentSize + "\nNum Workers:\t" + workers.length + workerStat
  }
}

case class JenkinsJob(override val queue:Queue, val delayLeft:Int, override val name:String)
	extends Job(queue, Config.jenkinsWorkers, Delay(Config.jenkinsDelay, delayLeft, false), name)

case class StormJob(override val queue:Queue, override val workers:Seq[Delay], override val delay:Delay, override val name:String)
	extends Job(queue, workers, delay, name)

case class Pipeline (val jobMap: Map[String, Job],val linkMap: Map[String, List[(Float, String)]]){
  def tick: Pipeline = {
    val tickedJobMap = jobMap.map { job =>
      (job._1 -> job._2.tick)
    }.toMap
    return Pipeline(tickedJobMap, linkMap)
  }

  def tock = {
    val tockedJobs = linkMap.map { link =>
//      if (jobMap(link._1).delay.delayTimeLeft == 0)
//      else
      List(("hi", 2))
    }
    tockedJobs
  }
}

object Pipeline {
  def buildFromLinks(links: List[Link]): Pipeline = {
    val linkMap = links.map( f => (f.job.name -> f.nextJobs.map( x => (x._1, x._2.name )))).toMap
    val jobMap = links.map( f => (f.job.name -> f.job)).toMap
    Pipeline(jobMap, linkMap)
  }
}


case class Link (val job: Job, val nextJobs: List[(Float, Job)]) {
  val total = nextJobs.foldLeft(0f)(_+_._1)
  if ( total > 1 ) {
    println("Link " + job + " adds to more than 1")
    exit(0)
  }
  def linkMap = (job.name, nextJobs.map(f => (f._1, f._2.name)))
}