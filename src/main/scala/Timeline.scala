package simulator;
import sys.process._
import java.io.FileWriter

class Timeline {
  var registrar:Map[Int, Map[String, Int]] = Map()
  var headers: Seq[String] = Seq()

  def addHeader(header:String) {
    headers = headers :+ header
  }

  def addHeaders(newHeaders:Seq[String]) {
    headers = headers ++ newHeaders
  }

  def register(timestamp:Int, data: Map[String, Int]) {
    registrar = registrar + (timestamp -> (registrar.getOrElse(timestamp, Map[String, Int]()) ++ data))
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

    new FileWriter(Config.outputTraceFileName, false)
    val outputTraceFile = new FileWriter(Config.outputTraceFileName, true)
    outputTraceFile.write(headers.foldLeft(",")((a,b) => a + b + ","))
    try {
      registrar.toSeq.sortBy(_._1).map( b =>
          outputTraceFile.write(headers.foldLeft("\n" + b._1 + ",")((x,y) => x + b._2.get(y).get + ","))
      )
    }
    finally outputTraceFile.close()


    val outputStats:String = "average,stdDev\n" + usageStats.get("avg").get + "," + usageStats.get("stdDev").get

    val outputStatsFile = new FileWriter(Config.outputStatsFileName, false)
    try {
      outputStatsFile write outputStats
    }
    finally outputStatsFile close
  }
}
