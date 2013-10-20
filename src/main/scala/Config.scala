package simulator

import java.util.Random

object Config {
  val jenkinsDelay = 300
  val jenkinsWorkerProcessingTime = 0
  val numJenkinsWorkers= 1

  val fraudProcessingTime = 6
  val numFraudWorkers = 4

  val assignSupplierProcessingTime = 3
  val numAssignSupplierWorkers = 1

  val consolidateShippingProcessingTime = 3
  val numConsolidateShippingWorkers = 1


  val apoProcessingTime = 3
  val apoJobProcessingTime = 3
  val numApoWorkers = 1

  val pipelineStarttime = 0;
  val pipelineEndtime = 11500;
  val pipelineRuntime = 11500;

  val outputTraceFileName = "output_trace.csv";
  val outputStatsFileName = "output_stats.csv";

  def orderLoad(time:Int): Int = {
    val rand = new Random();
    //      return Math.abs((rand.nextGaussian() + Config.pipelineRuntime/2)).toInt;
    var dist = time % Config.pipelineRuntime;
    if (dist > Config.pipelineRuntime/2) {
      dist = Config.pipelineRuntime - dist
    }
    val v = (rand.nextGaussian()*dist)/(Config.pipelineRuntime*.25)

    return Math.abs(v).toInt;
  }

}
