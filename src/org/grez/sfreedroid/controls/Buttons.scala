package org.grez.sfreedroid.controls

import org.grez.sfreedroid.drawable.Drawable
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.textures.{Color, Rect}
import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.console.DefaultConsole


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/4/12
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */

class TextRectButton(val text: String, override val rect: Rect, val action: ()=>Unit ) extends Control with Drawable {
  import rect._

  private val bodyColor = Color(0.8f, 0.5f, 0.2f);
  private val selectedBorderColor = Color(0.3f, 0.5f, 0.8f);
  private val textFont = "font05";
  private val selectedTextFont = "font05_blue";

  def draw() {
   // glShadeModel(GL_FLAT);

    drawButtonBody(bodyColor);
    if (isMouseOn) {
      drawButtonBorder(selectedBorderColor);
      drawText(selectedTextFont);
    } else {
      drawText(textFont);
    }
  }


 private def drawText(font: String) {
   FontManager.drawText(leftTop.getX + 5, leftTop.getY + 5,text, font);
 }

 private def drawButtonBorder(color: Color)  {
    import color._

    glColor3f(red, green, blue);

    glDisable(GL_TEXTURE_2D);
     glBegin(GL_LINES);
     glVertex2i(leftTop.getX, leftTop.getY);
     glVertex2i(rightTop.getX, rightTop.getY);

     glVertex2i(rightTop.getX, rightTop.getY);
     glVertex2i(rightBottom.getX, rightBottom.getY);

     glVertex2i(rightBottom.getX, rightBottom.getY);
     glVertex2i(leftBottom.getX, leftBottom.getY);

     glVertex2i(leftBottom.getX, leftBottom.getY);
     glVertex2i(leftTop.getX, leftTop.getY);

    glEnd();
    glEnable(GL_TEXTURE_2D);
  }

 private def drawButtonBody(color: Color) {
    import color._
    glColor3f(red, green, blue);

    glDisable(GL_TEXTURE_2D);
    glBegin(GL_QUADS);
     glVertex2i(leftTop.getX, leftTop.getY);
     glVertex2i(rightTop.getX, rightTop.getY);
     glVertex2i(rightBottom.getX, rightBottom.getY);
     glVertex2i(leftBottom.getX, leftBottom.getY);
    glEnd();

    glEnable(GL_TEXTURE_2D);
  }


  def mouseDown() {
    action();
  }

  def mouseUp() {

  }

  def getAnimDrawableSubstitute: AnimDrawableSubstitute = new AnimDrawableSubstitute {
    def draw(offsetX: Float, offsetY: Float) {
      glPushMatrix();
      glTranslatef(offsetX,offsetY, 0);
      drawButtonBody(bodyColor);
      drawText(textFont);
      glPopMatrix();
    }
  }
}

object Text2Rect {
  def apply(x: Int, y: Int, text: String, font: String): Rect = {
    Rect((x,y),(x+10+FontManager.getTextWidth(text,font), y+30))
  }
}

object ConsoleCMD {
  def apply(cmd: String): () => Unit = (() => DefaultConsole.execute(cmd));
}

class TextButton (override val text: String, val x: Int, val y: Int, cmd: String) extends TextRectButton(text, Text2Rect(x,y,text,"font05"), ConsoleCMD(cmd)) ;
class TextActionButton(override val text: String, val x: Int, val y: Int, override val action: ()=>Unit ) extends TextRectButton(text, Text2Rect(x,y,text,"font05"),action);

trait AnimDrawableSubstitute {
  def draw(offsetX: Float, offsetY: Float);
}