package main.scala
import simulator._
import java.util.Random

object Simulator {

  def main(args: Array[String]) {

    val t = Pipeline.process(Pipeline(Config.jobs, Config.links, Config.head), Config.orderLoad)

    t.toCSV()

  }
}
