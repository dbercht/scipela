package test.scala.link

import simulator.Link
import simulator._
import org.scalatest._

class LinkSpec extends FlatSpec{

  "A link" should "be valid if links add to one" in {
    val link = new Link("job", List(.3f -> "job", .7f-> "job"))

    assert(link.valid)
  }

  "A link" should "be valid if links add to more than one" in {
    val link = new Link("job", List(.4f -> "job", .7f-> "job"))

    assert(!link.valid)
  }

  "A link" should "be valid if links add to less than one" in {
    val link = new Link("job", List(.4f -> "job", .3f-> "job"))

    assert(!link.valid)
  }
}