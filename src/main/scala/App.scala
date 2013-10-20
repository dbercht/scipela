package main.scala
import simulator._
import java.util.Random

object Simulator {

  def makePipeline():Pipeline = {
    //val fraudJenkinsJob = Job.create()

    val fraudJenkinsJob = Job.fromConfig("fraud_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0)
    val fraudStormJob = Job.fromConfig("fraud_storm", Config.numFraudWorkers, Config.fraudProcessingTime, 0)

    val asJenkinsJob = Job.fromConfig("as_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0)
    val asStormJob = Job.fromConfig("as_storm", Config.numAssignSupplierWorkers, Config.assignSupplierProcessingTime, 0)

    val csJenkinsJob = Job.fromConfig("cs_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0)
    val csStormJob = Job.fromConfig("cs_storm", Config.numConsolidateShippingWorkers, Config.consolidateShippingProcessingTime, 0)

    val apoJenkinsJob = Job.fromConfig("apo_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0)
    val apoJob = Job.fromConfig("apo_job", 1, Config.apoJobProcessingTime, 0)
    val apoStorm = Job.fromConfig("apo_storm", Config.numApoWorkers, Config.apoProcessingTime, 0)


    val links = List(
      Link(fraudJenkinsJob, List((.2f -> asJenkinsJob), (.8f -> fraudStormJob))),
      Link(fraudStormJob, List((1f -> asJenkinsJob), (0f -> asStormJob))),
      Link(asJenkinsJob, List(1f -> asStormJob)),
      Link(asStormJob, List(1f -> csStormJob)),
      Link(csStormJob, List(1f -> apoJenkinsJob)),
      Link(apoJenkinsJob, List(1f -> apoJob))
    )

    Pipeline.buildFromLinks(links, fraudJenkinsJob)
  }


  def print(pipeline: Pipeline) {
    for (map <- pipeline.jobMap) {
      println (map)

    }
    println("-")
  }

  def main(args: Array[String]) {

    val t = Pipeline.process(makePipeline(), Config.orderLoad)

    t.toCSV()

  }
}