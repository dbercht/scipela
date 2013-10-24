package simulator

import java.util.Random

object Config {
  
  val delay = 150
  val processingTime = 0
  val numWorkers = 1


  val pipelineStarttime = 0;
  val pipelineEndtime = 86400;
  val pipelineRuntime = 86400;

  val outputTraceFileName = "output_trace.csv";
  val outputStatsFileName = "output_stats.csv";

  val orderLoadVariation = .50f;
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
