package main.scala
import simulator._
import java.util.Random

object Simulator {

  def makePipeline():Pipeline = {
    val jobs = Map(
      "step_1" -> Job.fromConfig("step_1", Config.numWorkers, Config.processingTime, Config.delay, 0),
      "step_2" -> Job.fromConfig("step_2", Config.numWorkers, Config.processingTime, Config.delay, 0),
      "step_3" -> Job.fromConfig("step_3", Config.numWorkers, Config.processingTime, Config.delay, 0)
    )

    val links = Map[String, List[(Float, String)]](
      "step_1" -> List((.2f -> "step_2"), (.8f -> "step_3"))
    )

    Pipeline(jobs, links, jobs.get("step_1").get )
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
