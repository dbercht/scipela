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
        case Delay(_, 0, _) =>
          val sum = job.workers.foldLeft(0)((sum,worker) =>
            worker match {
              case Delay(0, 0, _) => sum + job.queue.currentSize
              case Delay(_, 0, _) => sum + 1
              case _ => sum
            }
          )
          a + (b._1 -> (a.getOrElse(b._1, 0) + sum))
        case _ => a
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

  /**
   * Returns total workers being used by this pipeline
   *
   * @return Int the number of current active workers
   */
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
    val job = jobMap.get(head.name).get.feed(load)
    return updateJob(job)
  }
}

object Pipeline {

  def process(pipeline: Pipeline, load: Int => Int) : Timeline = {
    var t = new Timeline(pipeline.jobMap.map(f => f._2.name).toSeq);
    t.addHeader("load");
    t.addHeader("usage");

    def processRec(pipeline: Pipeline, r: Int) :Pipeline =  {
      if (r == Config.pipelineEndtime) {
        return pipeline.tick.tock
      }
      val currLoad = load(r);
      t.register(r, pipeline.jobMap.map(f => (f._2.name -> f._2.queue.currentSize)).toMap);
      t.register(r, Map("load" -> currLoad));
      t.register(r, Map("usage" -> pipeline.usage));
      pipeline.jobMap.map(f =>
        println(f)
      )

      processRec(pipeline.feed(currLoad).tick.tock, r + 1)
    }
    processRec(pipeline, Config.pipelineStarttime)
    return t
  }

}