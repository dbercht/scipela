package simulator;
import sys.process._
import java.io.FileWriter

class Timeline(var headers: Seq[String]) {
  var registrar:Map[Int, Map[String, Int]] = Map()

  def addHeader(header:String) {
    headers = headers :+ header
  }

  def register(timestamp:Int, data: Map[String, Int]) {
    if (registrar.contains(timestamp)) {
      registrar = registrar + (timestamp -> (registrar.getOrElse(timestamp, Map[String, Int]()) ++ data))
    } else {
      registrar = registrar + (timestamp -> data)
    }
  }

  def usageStats: Map[String, Double]= {
    import scala.math._

    def squaredDifference(value1: Double, value2: Double) = pow(value1 - value2, 2.0)

    def usage = registrar.map(f => f._2.get("usage").get.toDouble).toList
    def avg:Double = usage.reduceLeft(_ + _) / usage.length

    def stdDev(list: List[Double], average: Double) = list.isEmpty match {
      case false =>
        val squared = list.foldLeft(0.0)(_ + squaredDifference(_, average))
        sqrt(squared / list.length.toDouble)
      case true => 0.0
    }
    return Map("avg" -> avg, "stdDev" -> stdDev(usage, avg))
  }


  def toCSV() {
    var outputTrace:String = headers.foldLeft(",")((a,b) => a + b + ",")
    outputTrace += registrar.toSeq.sortBy(_._1).foldLeft("\n")((a,b) =>
      a + headers.foldLeft("\n" + b._1 + ",")((x,y) => x + b._2.get(y).get + ",")
    )

    var outputStats:String = "average,stdDev\n";
    outputStats += usageStats.get("avg").get + "," + usageStats.get("stdDev").get

    val outputTraceFile = new FileWriter(Config.outputTraceFileName, false)
    try {
      outputTraceFile.write(outputTrace)
    }
    finally outputTraceFile.close()

    val outputStatsFile = new FileWriter(Config.outputStatsFileName, false)
    try {
      outputStatsFile write outputStats
    }
    finally outputStatsFile close
  }
}
