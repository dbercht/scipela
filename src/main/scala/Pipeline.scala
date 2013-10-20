package simulator;

import scala.math.round;

case class Pipeline (val jobMap: Map[String, Job],val linkMap: Map[String, List[(Float, String)]], head: Job ){
  def tick: Pipeline = {
    val tickedJobMap = jobMap.map { job =>
      (job._1 -> job._2.tick.activate)
    }.toMap

    return Pipeline(tickedJobMap, linkMap, head)
  }

  def tock = {
    val jobSums = linkMap.foldLeft(Map[String, Int]()){(a, b) =>
      val job = jobMap.get(b._1).get
      if (jobMap.get(b._1).get.delay.delayTimeLeft == 0) {
        val sum = job.workers.foldLeft(0)((sum,worker) =>
          if (worker.delayTimeLeft == 0) {
            if (worker.delayTime ==0) {
              sum + job.queue.currentSize
            } else {
              sum + 1
            }
          } else {
            sum
          }
        )
        a + (b._1 -> (a.getOrElse(b._1, 0) + sum))
      } else{
        a
      }
    }
    val starvedJobs = jobSums.foldLeft(jobMap)((a,b) =>
      if (b._2 > 0 ) {
         a + (b._1 -> jobMap.get(b._1).get.starve(b._2) )
      } else {
        a
      }
    )

    val fedJobs = starvedJobs.foldLeft(starvedJobs)((feedingJobs, job) => {
      linkMap.getOrElse(job._2.name, Seq()).foldLeft(feedingJobs)((feedingLinks, link) =>
         feedingLinks + (feedingLinks.get(link._2).get.name -> feedingLinks.get(link._2).get.feed(round(link._1 * jobSums.getOrElse(job._2.name, 0)).toInt))
        )
      }
    )
    Pipeline(fedJobs, linkMap, head)
  }

  def usage: Int =  {
    jobMap.foldLeft(0)((sum, job) =>
      job._2.workers.foldLeft(sum)((cSum, worker) =>
        worker match {
          case Delay(_, _, true) => cSum + 1
          case _ => cSum
        }
      )
    )
  }

  def updateJob(job: Job): Pipeline = {
    new Pipeline(jobMap + (job.name -> job), linkMap, head)
  }

  def feed(load: Int): Pipeline = {
    val job = jobMap.get(head.name).get.feed(load)
    return updateJob(job)
  }
}

object Pipeline {


//  def buildFromLinks(links: List[Link], head:Job): Pipeline = {
//    val linkMap = links.map( f => (f.job.name -> f.nextJobs.map( x => (x._1, x._2 )))).toMap
//    val jobMap = links.foldLeft(
//      links.map( f => (f.job.name -> f.job))
//    ){ (a, b) =>
//      b.nextJobs.map ( x =>
//         (x._2.name -> x._2 )
//     ) ++ a
//    }.toMap
//    Pipeline(jobMap, linkMap, head)
//  }

  def process(pipeline: Pipeline, load: Int => Int) : Timeline = {
    var t = new Timeline(pipeline.jobMap.map(f => f._2.name).toSeq);
    t.addHeader("load");
    t.addHeader("usage");

    def processRec(pipeline: Pipeline, r: Int) :Pipeline =  {
      if (r == Config.pipelineEndtime) {
        return pipeline.tock.tick
      }
      val currLoad = load(r);
      t.register(r, pipeline.jobMap.map(f => (f._2.name -> f._2.queue.currentSize)).toMap);
      t.register(r, Map("load" -> currLoad));
      t.register(r, Map("usage" -> pipeline.usage));
      processRec(pipeline.feed(currLoad).tick.tock, r + 1)
    }
    processRec(pipeline, Config.pipelineStarttime)
    return t
  }

}