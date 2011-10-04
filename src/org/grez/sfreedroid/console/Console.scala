package org.grez.sfreedroid.console

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.font.FontManager
import org.lwjgl.input.Keyboard
import collection.immutable.Queue

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/4/11
 * Time: 5:58 PM
 * console
 */

class Console(val height:Int, val histSize:Int)  {


  val FONT_HEG = 25;
  val LINE_H = (FONT_HEG+4);
  val textLinesCount: Int = (height / LINE_H)-1;
  var showing = false
  var cmd: String = "";
  var historyText: Queue[String] = Queue("");
  var histPosition = 0;

  private def histUp(){
    if (histPosition >= historyText.size - textLinesCount) return;
    histPosition += 1;
  }

  private def histDown(){
    if (histPosition <= 0) return;
    histPosition -= 1;
  }

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

  def log (value : Any){
    println(value);

    def putTx(txt: String){
      historyText += txt;
      if (historyText.size > histSize) historyText = historyText.dequeue._2;
      }

    var txt = value.toString
    while (!txt.isEmpty){
        val kk = txt.splitAt(80)
        txt = kk._2;
        putTx(if (kk._1.length() >79) kk._1+'/' else kk._1 );
      }
  }

  private def drawCmd() {
     FontManager.drawText(2,height-FONT_HEG,cmd+'_', "redfont" );
  }

  def addCh(key: Int, c: Char) {
    import org.lwjgl.input.Keyboard._

    cmd = key match {
      case KEY_BACK  => cmd.take(cmd.size - 1) ;
      case KEY_SPACE => cmd + ' ' ;
      case KEY_GRAVE => cmd ;
      case KEY_PRIOR => {
        histUp();
        cmd;
      };
      case KEY_NEXT => {
        histDown();
        cmd;
      }
      case KEY_RETURN => {
        log(cmd);
        ""
      };
      case _ => c match {
        case pc if FontManager.printableChar(pc,"redfont") => cmd+c;
        case _ => cmd;
      };
    }
  }

  def drawHistText(){

    def recur(txt: Queue[String], yidx: Int){
      if (txt.isEmpty || yidx < -LINE_H) return;

      val elem = txt.dequeue;
      FontManager.drawText(2,yidx,elem._1, "redfont" );
      recur(elem._2, yidx - FONT_HEG)
    }

    recur(historyText.reverse.drop(histPosition), textLinesCount * LINE_H);

  }

  def draw(){
      drawRectangle();
      drawHistText();
      drawCmd();
  }
}

object DefaultConsole extends Console(200,1000);