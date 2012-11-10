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

class TextRectButton(val text: String, val rect: Rect, val action: ()=>Unit ) extends Control with Drawable {
  import rect._

  def draw() {
   // glShadeModel(GL_FLAT);

    drawButtonBody(Color(0.8f, 0.5f, 0.2f));
    if (isMouseOn) {
      drawButtonBorder(Color(0.3f, 0.5f, 0.8f));
      FontManager.drawText(leftTop.getX + 5, leftTop.getY + 5,text, "font05_blue");
    } else {
      FontManager.drawText(leftTop.getX + 5, leftTop.getY + 5,text, "font05");
    }
  }


  def drawButtonBorder(color: Color)  {
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

  def drawButtonBody(color: Color) {
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
}

object Text2Rect {
  def apply(x: Int, y: Int, text: String, font: String): Rect = {
    Rect((x,y),(x+10+FontManager.getTextWidth(text,font), y+30))
  }
}

object ConsoleCMD {
  def apply(cmd: String): () => Unit = (() => DefaultConsole.execute(cmd));
}

class TextButton (override val text: String, val x: Int, val y: Int, cmd: String) extends TextRectButton(text, Text2Rect(x,y,text,"font05"), ConsoleCMD(cmd)) {

}
