package main.scala
import simulator._
import java.util.Random

object Simulator {

  def makePipeline():Pipeline = {
    val jobs = Map(
      "step_1" -> Job.fromConfig("step_1", Config.numWorkers, Config.processingTime, Config.delay, 0),
      "step_2" -> Job.fromConfig("step_2", 2, 4, 0, 0),
      "step_3" -> Job.fromConfig("step_3", Config.numWorkers, Config.processingTime, Config.delay)
//      "step_4" -> Job.fromConfig("step_4", 2, 4, 0, 0)
    )

    val links = Map[String, List[(Float, String)]](
      "step_1" -> List((1f -> "step_2")),
      "step_2" -> List((1f -> "step_3"))
//      "step_3" -> List((1f -> "step_4"))
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
