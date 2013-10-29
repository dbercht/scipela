package test.scala.delay

import simulator.{Delay}
import org.scalatest._

class DelaySpec extends FlatSpec {
  "A Delay" should "have reset delay left if not munching" in {
    val delay = new Delay(54, 20, false)
    val delayNotMunching = delay.decrement
    assert(delayNotMunching.delayTimeLeft === delay.delayTime)
    assert(!delayNotMunching.munching)
  }

  it should "reset if decremented from zero" in {
    val delay = new Delay(54, 0, true)
    val delayMunching = delay.decrement
    assert(delayMunching.delayTimeLeft === delay.delayTime)
    assert(!delayMunching.munching)
  }

  it should "decrement by one if del" in {
    val delay = new Delay(54, 20, true)
    val delayMunching = delay.decrement
    assert(delayMunching.delayTimeLeft === delay.delayTimeLeft - 1)
    assert(delayMunching.munching)
  }
}