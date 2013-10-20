package main.scala
import simulator._
import java.util.Random

object Simulator {

  def makePipeline():Pipeline = {
    //val fraudJenkinsJob = Job.create()

    val jobs = Map(
      "fraud_jenkins" -> Job.fromConfig("fraud_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0),
      "fraud_storm" -> Job.fromConfig("fraud_storm", Config.numFraudWorkers, Config.fraudProcessingTime, 0),
      "as_jenkins" -> Job.fromConfig("as_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0),
      "as_storm" -> Job.fromConfig("as_storm", Config.numAssignSupplierWorkers, Config.assignSupplierProcessingTime, 0, 0),
      "cs_jenkins" -> Job.fromConfig("cs_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0),
      "cs_storm" -> Job.fromConfig("cs_storm", Config.numConsolidateShippingWorkers, Config.consolidateShippingProcessingTime, 0),
      "apo_jenkins" -> Job.fromConfig("apo_jenkins", Config.numJenkinsWorkers, Config.jenkinsWorkerProcessingTime, Config.jenkinsDelay, 0),
      "apo_job" -> Job.fromConfig("apo_job", 1, Config.apoJobProcessingTime, 0),
      "apo_storm" -> Job.fromConfig("apo_storm", Config.numApoWorkers, Config.apoProcessingTime, 0)
    )

    val links = Map[String, List[(Float, String)]](
      "fraud_jenkins" -> List((.2f -> "as_jenkins"), (.8f -> "fraud_storm")),
      "fraud_storm" -> List((0f -> "as_jenkins"), (1f -> "as_storm")),
      "as_jenkins" -> List(1f -> "as_storm"),
//      "as_storm" -> List(1f -> "cs_jenkins"),
      "as_storm" -> List(1f -> "cs_storm"),
      //      "cs_jenkins" -> List(1f -> "cs_storm"),
      "cs_storm" -> List(1f -> "apo_storm"),
      "apo_jenkins" -> List(1f -> "apo_storm")
    )

    Pipeline(jobs, links, jobs.get("fraud_storm").get )
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