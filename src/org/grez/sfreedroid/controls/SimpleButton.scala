package org.grez.controls

import org.grez.sfreedroid.drawable.Drawable
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.textures.Rect
import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.console.DefaultConsole
import org.grez.sfreedroid.controls.Control


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/4/12
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */

class SimpleButton(val rect: Rect) extends Control with Drawable{

  def draw() {
    //  glPushMatrix();
    //  glTranslatef(0.0f, 0.0f, 0.0f)
    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    //glEnable(GL_BLEND);
    //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    if (isMouseOn) {
      glColor3f(0.8f, 0.5f, 0.2f);
    } else {
      glColor3f(0.3f, 0.5f, 0.8f);
    }

    glBegin(GL_QUADS);
    import rect._

    glVertex2i(leftTop.getX, leftTop.getY);
    glVertex2i(rightTop.getX, rightTop.getY);
    glVertex2i(rightBottom.getX, rightBottom.getY);
    glVertex2i(leftBottom.getX, leftBottom.getY);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    //   glPopMatrix();
    //glDisable(GL_BLEND);
  }


  def mouseDown() {
    DefaultConsole.log("Button Down!");
  }

  def mouseUp() {
    DefaultConsole.log("Button UP!");
  }
}

class TextRectButton(val text: String, val rect: Rect, val action: ()=>Unit ) extends Control with Drawable {
  import rect._

  def draw() {
   // glShadeModel(GL_FLAT);

    drawButtonBody(0.8f, 0.5f, 0.2f);
    if (isMouseOn) {
      drawButtonBorder(0.3f, 0.5f, 0.8f);
      FontManager.drawText(leftTop.getX + 5, leftTop.getY + 5,text, "font05_blue");
    } else {
      FontManager.drawText(leftTop.getX + 5, leftTop.getY + 5,text, "font05");
    }
  }


  def drawButtonBorder(red: Float, green: Float, blue: Float)  {
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

  def drawButtonBody(red: Float, green: Float, blue: Float) {
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
