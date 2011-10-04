package org.grez.sfreedroid.console

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.font.FontManager
import org.lwjgl.input.Keyboard

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/4/11
 * Time: 5:58 PM
 * console
 */

class Console(val height:Int, val histSize:Int)  {




  val FONT_HEG = 25;
  var showing = false
  var cmd: String = "";

  private def drawRectangle(){
    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glColor4f(0.3f, 0f, 0.8f,0.8f);
    glBegin(GL_QUADS);
    glVertex2i(0,0);
    glVertex2i(1024,0);
    glVertex2i(1024,height);
    glVertex2i(0,height);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    glDisable(GL_BLEND);
  }

  private def drawCmd() {
     FontManager.drawText(2,height-FONT_HEG,cmd+'_', "redfont" );
  }

  def addCh(key: Int, c: Char) {
    import org.lwjgl.input.Keyboard._

    cmd = key match {
      case KEY_BACK => { cmd.take(cmd.size - 1) }
      case KEY_SPACE => {cmd + ' '}
      case KEY_GRAVE => {cmd}
      case _ => { c match {
        case pc if FontManager.printableChar(pc,"redfont") => { cmd+c }
        case _ => cmd;
      }}
    }
  }

  def draw(){
      drawRectangle();
      drawCmd();
  }
}