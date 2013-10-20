package simulator

case class Delay (val delayTime:Int, val delayTimeLeft:Int, val munching:Boolean) {
  def decrement: Delay = {
    if (!munching || delayTimeLeft < 1) {
      Delay(delayTime, delayTime, false)
    } else {
      Delay(delayTime, delayTimeLeft - 1, true)
    }
  }
  def munch: Delay = {
    Delay(delayTime, delayTimeLeft, true)
  }
}

class JenkinsWorker extends Delay(0, 0, false)
class StormDelay extends Delay(0, 0, false)

