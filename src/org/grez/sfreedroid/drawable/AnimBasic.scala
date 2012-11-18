package org.grez.sfreedroid.drawable

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.textures.Color

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

trait TranslateAnim extends Drawable with AnimDrawableSubstitute {
  override def draw(offsetX: Float, offsetY: Float){
        glPushMatrix();
        glTranslatef(offsetX, offsetY, 0);
        draw();
        glPopMatrix();
  }
}

class ClickAnim(val x: Int, val y: Int, val color: Color, override val callBack: () => Unit) extends AnimBasic {

  private val MAX_STEP = 50;
  override var stage = 0;
  override val step = 1;

  override val checkLbd: (Int) => Boolean =  ((i: Int) => i >= MAX_STEP);


  def drw() {
    import color._

    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    glColor3f(red, green, blue);

    glPushMatrix();
    glTranslatef(x,y,0.0f)
    glRotatef(stage  * 5, 0.0f,0.0f,1.0f);

    glBegin(GL_LINES);
     glVertex2i(-10,10);
     glVertex2i(+10,-10);

     glVertex2i(-10,-10);
     glVertex2i(+10,+10);

    glEnd();
    glPopMatrix();
    glEnable(GL_TEXTURE_2D);
  }

}
