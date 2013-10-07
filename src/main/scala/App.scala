package main.scala
import simulator.entities._

object Simulator {

  def makePipeline():Pipeline = {
    val emptyQ = Queue(0)

    val fraudJenkinsJob = JenkinsJob(emptyQ, Config.jenkinsDelay, "fraud_jenkins")
    val fraudStormWorker = Delay(Config.fraudProcessingTime, Config.fraudProcessingTime, false)
    val fraudStormJob = StormJob(emptyQ, Seq(fraudStormWorker, fraudStormWorker), new StormDelay, "fraud_storm")

    val asJenkinsJob = JenkinsJob(emptyQ, Config.jenkinsDelay, "as_jenkins")
    val asStormWorker = Delay(Config.assignSupplierProcessingTime, Config.assignSupplierProcessingTime, false)
    val asStormJob = StormJob(emptyQ, Seq(asStormWorker, asStormWorker), new StormDelay, "as_storm")
    val lastJob = JenkinsJob(emptyQ, Config.jenkinsDelay, "last_job")

    val links = List(
      Link(fraudJenkinsJob, List((.3f -> asJenkinsJob), (.7f -> fraudStormJob))),
      Link(fraudStormJob, List(1f -> asJenkinsJob)),
      Link(asJenkinsJob, List(1f -> asStormJob)),
      Link(asJenkinsJob, List(1f -> asStormJob)),
      Link(asStormJob, List(1f -> lastJob))
    )

    Pipeline.buildFromLinks(links)
  }


  def print(pipeline: Pipeline) {
    println(pipeline.jobMap)
  }

  def main(args: Array[String]) {
    val pipeline = makePipeline()
    print(pipeline)
//    println(pipeline)
    println(pipeline.tock)
  }
}