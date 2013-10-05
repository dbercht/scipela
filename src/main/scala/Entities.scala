package simulator.entities

object Config {
  val jenkinsDelay = 5
  val jenkinsWorkerProcessingTime = 0
  val jenkinsWorkers = Seq(Worker(Config.jenkinsWorkerProcessingTime, Config.jenkinsWorkerProcessingTime))
  
  val assignSupplierProcessingTime = 3
  val fraudProcessingTime = 3
}

case class Worker (val timeToProcess:Int, val timeToProcessLeft:Int)
case class Delay (val delayTime:Int, val delayTimeLeft:Int)
class StormDelay extends Delay(0, 0)
case class Queue (val currentSize:Int)

class Job (val queue:Queue, val workers:Seq[Worker], val delay: Delay, val name:String) {
  override def toString():String = {
    val workerStat = workers.foldLeft("\nTime Left:\n")(_+"\t" + _.timeToProcessLeft + "\n")
    return "Name:\t" + name + "\nQueue Length:\t" + queue.currentSize + "\nNum Workers:\t" + workers.length + workerStat
  }
}

case class JenkinsJob(override val queue:Queue, val delayLeft:Int, override val name:String)
	extends Job(queue, Config.jenkinsWorkers, Delay(Config.jenkinsDelay, delayLeft), name)

case class StormJob(override val queue:Queue, override val workers:Seq[Worker], override val delay:Delay, override val name:String)
	extends Job(queue, workers, delay, name)

case class Pipeline (val links: List[Link]){
  def addLink(link: Link):Pipeline = {
    Pipeline(links :+ link)
  }
}

class Link (val job: Job, val nextJobs: List[(Float, Job)]) {
  def linkMap = (job.name, nextJobs.map(f => (f._1, f._2.name)))  
}

case class MultipleLinks (override val job: Job, override val nextJobs: List[(Float, Job)]) extends Link (job, nextJobs) {
  val total = nextJobs.foldLeft(0f)(_+_._1)
  if ( total > 1 ) {
     println("Link " + job + " adds to more than 1")   
     exit(0)
  }
}
case class SingleLink(override val job: Job, val nextJob: Job) extends Link (job, List(1f -> nextJob))


