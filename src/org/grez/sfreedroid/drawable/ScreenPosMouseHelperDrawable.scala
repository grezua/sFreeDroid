package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.controls.OnMousePosUpdate
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.debug.GlobalDebugState
import org.lwjgl.util.Point
import math._

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/28/12
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */

object defConstants {
  val tan_22_5 = tan(toRadians(22.5)).toFloat;
}

abstract class SideSpec {

  type FPoint = (Float,Float);

  def getSidePoints(c: CalcScreensConstStructure) : (FPoint,FPoint);
  def isMouseAngleIn(angle: Double): Boolean;
  def getRobotIndex: Int;
}

class CalcScreensConstStructure(val sx: Int, val sy: Int){
  import defConstants.tan_22_5;

  val (heightTop, heightBottom) = (sy, 768 - sy);
  val (widthLeft, widthRight) =   (sx, 1024 - sx);

  val heightLeft = tan_22_5 * widthLeft;
  val widthTop = tan_22_5 * heightTop;
  val heightRight = tan_22_5 * widthRight;
  val widthBottom = tan_22_5 * heightBottom;

}

object TopSideSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((sx-widthTop,0), (sx+widthTop,0))
  }

  def isMouseAngleIn(angle: Double) = ( angle < -157.5 && angle >= -180 )|| (angle <= 180 && angle >= 157.5)

  def getRobotIndex = 4
}

object TopRightSideSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((sx+widthTop,0),(1024,sy-heightRight))
  }

  def isMouseAngleIn(angle: Double) =  (angle < 157.5 && angle >= 112.5)

  def getRobotIndex = 3
}

object RightSideSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((1024,sy-heightRight), (1024,sy+heightRight))
  }

  def isMouseAngleIn(angle: Double) =  (angle < 112.5 && angle >= 67.5)

  def getRobotIndex = 2
}

object RightBottomSideSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((1024,sy+heightRight),(sx+widthBottom,768))
  }

  def isMouseAngleIn(angle: Double) =  (angle < 67.5 && angle >= 22.5)

  def getRobotIndex = 1
}

object BottomSideSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((sx+widthBottom,768),(sx-widthBottom,768))
  }

  def isMouseAngleIn(angle: Double) =  (angle < 22.5 && angle >= -22.5)

  def getRobotIndex = 0
}

object BottomLeftSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((sx-widthBottom,768),(0,sy+heightLeft))
  }

  def isMouseAngleIn(angle: Double) =  (angle < -22.5 && angle >= -67.5)

  def getRobotIndex = 7
}

object LeftSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((0,sy+heightLeft),(0,sy-heightLeft))
  }

  def isMouseAngleIn(angle: Double) =  (angle < -67.5 && angle >= -112.5)

  def getRobotIndex = 6
}

object LeftTopSpec extends SideSpec {
  def getSidePoints(c: CalcScreensConstStructure) = {
    import c._;
    ((0,sy-heightLeft),(sx-widthTop,0))
  }

  def isMouseAngleIn(angle: Double) =  (angle < -112.5 && angle >= -157.5)

  def getRobotIndex = 5
}

object AllSpecs {
  val a = List(TopSideSpec,TopRightSideSpec,RightSideSpec,RightBottomSideSpec,
    BottomSideSpec,BottomLeftSpec,LeftSpec,LeftTopSpec);
  def apply() = a;
}

class ScreenPosMouseHelperDrawable(robot: RobotDrawable) extends Drawable with OnMousePosUpdate {

  import scala.math._;

  val tan_22_5 = tan(toRadians(22.5));

  def draw() {
    val (sx, sy) = GlobalDebugState.meScreenPost;
    val mouseAngle = toDegrees(atan2(mouseX - sx,mouseY - sy))

    val screenConst = new  CalcScreensConstStructure(sx,sy);
    import screenConst._;

    val (ltx, lty) = if (heightTop > widthLeft) (sx - heightTop, 0) else (0, sy - widthLeft);
    val (rtx, rty) = if (heightTop > widthRight) (sx + heightTop, 0) else (1024, sy - widthRight);

    val (lbx, lby) = if (heightBottom > widthLeft) (sx - heightBottom, 768) else (0, sy + widthLeft);
    val (rbx, rby) = if (heightBottom > widthRight) (sx + heightBottom, 768) else (1024, sy + widthRight);


    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LINE_SMOOTH);
    glColor3f(0f, 0f, 0f);
    glBegin(GL_LINES);
    {
      glVertex2i(sx, 0);
      glVertex2i(sx, 768);

      glVertex2i(0, sy);
      glVertex2i(1024, sy);

      glVertex2i(sx, sy);
      glVertex2i(rtx, rty);

      glVertex2i(sx, sy);
      glVertex2i(ltx, lty);

      glVertex2i(sx, sy);
      glVertex2i(rbx, rby);

      glVertex2i(sx, sy);
      glVertex2i(lbx, lby);

      AllSpecs().foreach(s => {
        val (p1, p2) = s.getSidePoints(screenConst);

        glVertex2i(sx, sy);
        glVertex2f(p1._1, p1._2);

        glVertex2i(sx, sy);
        glVertex2f(p2._1, p2._2);

        if (s.isMouseAngleIn(mouseAngle)) {

          glEnd();
          glColor4f(0.02f, 0.28f, 0.45f, 0.6f);
          glBegin(GL_TRIANGLES);
          {
            glVertex2i(sx, sy);
            glVertex2f(p1._1, p1._2);
            glVertex2f(p2._1, p2._2);
          }
          glEnd();
          glColor4f(0f, 0f, 0f, 1f);
          glBegin(GL_LINES);
        }
      });

    }
    glEnd();
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_LINE_SMOOTH);


  }
}
