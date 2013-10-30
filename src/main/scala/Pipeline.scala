package simulator;

import scala.math.round;

case class Pipeline (val jobMap: Map[String, Job],val linkMap: Map[String, List[(Float, String)]], head: Job ){
  /**
   * Ticks the pipeline by activating all jobs in the system
   *
   * @return Pipeline ticked pipeline
   */
  def tick: Pipeline = {
    val tickedJobMap = jobMap.map { job =>
      (job._1 -> job._2.tick.activate)
    }.toMap

    return Pipeline(tickedJobMap, linkMap, head)
  }

  /**
   * Tocks the pipeline by transitioning internal queue sizes based on worker delays
   *
   * @return Pipeline tocked pipeline
   */
  def tock = {
    val jobSums = jobMap.foldLeft(Map[String, Int]()){(a, b) =>
      val job = b._2
      job.delay match {
        case Delay(_, 0, _) => a + (b._1 -> (a.getOrElse(b._1, 0) + job.tockFeed))
        case _ => a
      }

    }

    val starvedJobs = jobSums.foldLeft(jobMap)((a,b) =>
      if (b._2 > 0 ) {
         a + (b._1 -> jobMap(b._1).starve(b._2) )
      } else {
        a
      }
    )

    val fedJobs = starvedJobs.foldLeft(starvedJobs)((feedingJobs, job) => {
      linkMap.getOrElse(job._2.name, Seq()).foldLeft(feedingJobs)((feedingLinks, link) =>
         feedingLinks + (feedingLinks(link._2).name -> feedingLinks(link._2).feed(round(link._1 * jobSums.getOrElse(job._2.name, 0)).toInt))
        )
      }
    )
    Pipeline(fedJobs, linkMap, head)
  }

  /**
   * Returns total workers being used by this pipeline
   *
   * @return Int the number of current active workers
   */
  def usage: Int =  {
    jobMap.foldLeft(0)((sum, job) =>
      job._2.activeWorkers + sum
    )
  }

  /**
   * Updates individual Job within pipeline
   * @param Job job Job to update
   * @return Pipeline with updated job map
   */
  def updateJob(job: Job): Pipeline = {
    new Pipeline(jobMap + (job.name -> job), linkMap, head)
  }

  /**
   * Feeds this Pipeline's head job
   *
   * @param Int load The load to feed to this pipeline's head queue.
   * @return Pipeline with feeding
   */
  def feed(load: Int): Pipeline = {
    val job = jobMap(head.name).feed(load)
    return updateJob(job)
  }

  def getStats: Map[String, Int] = {
    (jobMap.map(f => (f._2.name -> f._2.queue.currentSize)).toMap + ("usage" -> usage))
  }
}

object Pipeline {

  def process(pipeline: Pipeline, trace: Int => Int) : Timeline = {
    val t = new Timeline;
    t.addHeader("load")
    t.addHeaders(Seq("usage") ++ pipeline.jobMap.map(f => f._2.name).toSeq)

    def processRec(pipeline: Pipeline, r: Int) :Pipeline =  {
      if (r == Config.pipelineEndtime) {
        return pipeline.tick.tock
      }
      val currLoad = trace(r);
      t.register(r, pipeline.getStats);
      t.register(r, Map("load" -> currLoad));
      pipeline.jobMap.map(f =>
        println(f._2)
      )
      Thread.sleep(Config.delayTime)
      processRec(pipeline.tick.tock.feed(currLoad), r + 1)
    }
    processRec(pipeline, Config.pipelineStarttime)
    return t
  }

}