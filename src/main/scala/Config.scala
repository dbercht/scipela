package simulator

import java.util.Random

object Config {
  
  val delay = 150
  val processingTime = 0
  val numWorkers = 1

  val pipelineStarttime = 41000;
  val pipelineEndtime = 42000;
  val pipelineRuntime = 86400;


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

  def head = jobs.get("step_1").get

  val outputTraceFileName = "output_trace.csv";
  val outputStatsFileName = "output_stats.csv";

  val orderLoadVariation = .80f;
  def orderLoad(time:Int): Int = {
    val rand = new Random();

    def getLoadForTime(timeS: Int): Int = {
      var dist = timeS % Config.pipelineRuntime;
      if (dist > Config.pipelineRuntime/2) {
        dist = Config.pipelineRuntime - dist
      }
      val v = (rand.nextGaussian()*dist)/(Config.pipelineRuntime*Config.orderLoadVariation)
      return Math.abs(v).toInt
    }


    return (Math.abs(rand.nextGaussian()/2).toInt + getLoadForTime(time) + getLoadForTime(time - 2*60*60) + getLoadForTime(time - 3*60*60) + getLoadForTime(time + 60*60));
  }

}
