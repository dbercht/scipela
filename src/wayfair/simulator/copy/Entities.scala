package wayfair.simulator.copy

object Config {
  val jenkinsDelay = 5
  val jenkinsWorkerProcessingTime = 0
  val jenkinsWorkers = Seq(Worker(Config.jenkinsWorkerProcessingTime, Config.jenkinsWorkerProcessingTime))
  
  val assignSupplierProcessingTime = 3
}

case class Worker (val timeToProcess:Int, val timeToProcessLeft:Int)
case class Delay (val delayTime:Int, val delayTimeLeft:Int)
class StormDelay extends Delay(0, 0)
case class Queue (val currentSize:Int)

class Job (val queue:Queue, val workers:Seq[Worker], val delay: Delay)

case class JenkinsJob(override val queue:Queue, val delayLeft:Int)
	extends Job(queue, Config.jenkinsWorkers, Delay(Config.jenkinsDelay, delayLeft))

case class StormJob(override val queue:Queue, override val workers:Seq[Worker], override val delay:Delay)
	extends Job(queue, workers, delay)

case class Pipeline (val links: List[Link]){
  def addLink(link: Link) {
    Pipeline(links :+ link)
  }
}

abstract class Link (val job: Job, val next: List[(Float, Queue)]) { println("Hey")}
case class MultipleLinks (override val job: Job, override val next: List[(Float, Queue)]) extends Link (job, next) {
  val total = next.foldLeft(0f)(_+_._1)
//  if ( total > 1 )
     println("Link " + job + " add to more than 1")     
}
case class SingleLink(override val job: Job, val nextQ: Queue) extends Link (job, List(1f -> nextQ))


object Simulator {
  
  def makePipeline(){
    val pipeline = Pipeline(List())
    val fraudJenkinsQ = Queue(0)
    val fraudJenkinsJob = JenkinsJob(fraudJenkinsQ, Config.jenkinsDelay)

    val asJenkinsQ = Queue(0)
    val asJenkinsJob = JenkinsJob(asJenkinsQ, Config.jenkinsDelay)
    
//    pipeline.addLink(Link(fraudJenkinsJob, asJenkinsQ))

    
    val asStormQ = Queue(0)
    val asStormWorker = Worker(Config.assignSupplierProcessingTime, Config.assignSupplierProcessingTime)
    val asStormJob = StormJob(asStormQ, Seq(asStormWorker, asStormWorker), new StormDelay)
  }
  
  def main(args: Array[String]) {
    println("HI")
    
    val fraudJenkinsJob = JenkinsJob(Queue(0), Config.jenkinsDelay)
    MultipleLinks(fraudJenkinsJob, List((.9f -> Queue(1)), (.9f -> Queue(1))))
  }
}