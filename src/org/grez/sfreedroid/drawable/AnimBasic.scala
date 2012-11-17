package org.grez.sfreedroid.drawable

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/17/12
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AnimBasic() extends Drawable {
  protected val callBack: () => Unit;
  protected val step: Int;
  protected val checkLbd: (Int) => Boolean;

  protected var stage: Int;

  def drw();

 protected var stop = false;

  def draw() {
    drw();
    processAnim();
  }

  protected def processAnim() {
    if (checkLbd(stage) && !stop) {
      callBack();
      stop = true;
    } else {
      stage += step;
    }
  }
}

trait AnimDrawableSubstitute {
  def draw(offsetX: Float, offsetY: Float);
}
