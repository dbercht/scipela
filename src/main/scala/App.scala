package main.scala
import simulator.entities._

object Simulator {

  def makePipeline():Pipeline = {
    val pipeline = Pipeline(List())

    val fraudJenkinsQ = Queue(0)
    val fraudJenkinsJob = JenkinsJob(fraudJenkinsQ, Config.jenkinsDelay, "fraud_jenkins")
    val fraudStormQ = Queue(0)
    val fraudStormWorker = Worker(Config.fraudProcessingTime, Config.fraudProcessingTime)
    val fraudStormJob = StormJob(fraudStormQ, Seq(fraudStormWorker, fraudStormWorker), new StormDelay, "fraud_storm")

    val asJenkinsQ = Queue(0)
    val asJenkinsJob = JenkinsJob(asJenkinsQ, Config.jenkinsDelay, "as_jenkins")
    val asStormQ = Queue(0)
    val asStormWorker = Worker(Config.assignSupplierProcessingTime, Config.assignSupplierProcessingTime)
    val asStormJob = StormJob(asStormQ, Seq(asStormWorker, asStormWorker), new StormDelay, "as_storm")

    pipeline
    		.addLink(MultipleLinks(fraudJenkinsJob, List((.3f -> asJenkinsJob), (.7f -> fraudStormJob))))
    		.addLink(SingleLink(fraudStormJob, asJenkinsJob))
    		.addLink(SingleLink(asJenkinsJob, asStormJob))
  }

  def tick(pipeline: Pipeline) {
    println(pipeline.links.length)
    for (link <- pipeline.links) yield {
      link match {
        case MultipleLinks(job: JenkinsJob, next: List[(Float, Job)]) => println(job.toString)
        case MultipleLinks(job: StormJob, next: List[(Float, Job)]) => println(job.toString)
        case SingleLink(job: Job, nextJob: Job) => println(job.toString)
      }
    }
  }
  
  def print(pipeline: Pipeline) {
    val pipeStr = for(link <- pipeline.links) yield {
      println(link.linkMap.toString)
    }
  }

  def main(args: Array[String]) {
    val pipeline = makePipeline()
    val pipeline2 = print(pipeline)

  }
}