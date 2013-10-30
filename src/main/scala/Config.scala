package simulator

import java.util.Random

object Config {
  
  val delay = 150
  val processingTime = 0
  val numWorkers = 1

  val pipelineStarttime = 45500;
  val pipelineEndtime = 46000;
  val pipelineRuntime = 86400;


  val jobs = Map(
    "fraud_jenkins" -> Job.fromConfig("fraud_jenkins", Config.numWorkers, Config.processingTime, Config.delay, 75),
    "fraud_storm" -> Job.fromConfig("fraud_storm", 4, 5, 0, 0),
    "as_jenkins" -> Job.fromConfig("as_jenkins", Config.numWorkers, Config.processingTime, Config.delay)
//    "as_storm" -> Job.fromConfig("as_storm", 4, 4, 0, 0)
  )

  val links = Map[String, List[(Float, String)]](
    "fraud_jenkins" -> List((1f -> "fraud_storm")),
    "fraud_storm" -> List((1f -> "as_jenkins"))
//    "as_jenkins" -> List((1f -> "as_storm"))
  )

  def head = jobs.get("fraud_jenkins").get

  val outputTraceFileName = "output_trace.csv";
  val outputStatsFileName = "output_stats.csv";

  val orderLoadVariation = .70f;

  def orderLoads(time: Int): Int = {
    time % 5 match {
      case 0 => 1
      case _ => 0
    }
  }

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
