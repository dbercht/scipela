package simulator;

case class Link (val job: Job, val nextJobs: List[(Float, Job)]) {
  val valid = (nextJobs.foldLeft(0f)(_+_._1) == 1)
  def linkMap = (job.name, nextJobs.map(f => (f._1, f._2.name)))
}