package simulator;

case class Link (val job: String, val nextJobs: List[(Float, String)]) {
  val valid = (nextJobs.foldLeft(0f)(_+_._1) == 1)
  def linkMap = (job, nextJobs.map(f => (f._1, f._2)))
}