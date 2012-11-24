package org.grez.sfreedroid.utils

import org.lwjgl.opengl.GL11._
import java.util.Random
import collection.mutable.Queue
import org.grez.sfreedroid.drawable.Drawable
import org.grez.sfreedroid.font.FontManager

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/2/11
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */

class FPSMeter()  {
  private val MAX_HIST_SIZE = 65;
  private val MAX_HEG = 100;
  private val SECOND = 1000L;

  val histData: Queue[Int] = new Queue[Int]();
//  var currentPos = 0;
/*  val r: Random = new Random();
  for (i <- 0 until  MAX_HIST_SIZE) {
    histData(i) = r.nextInt(MAX_HEG);
  }*/

  var fps = 0;

  private var time: Long = System.currentTimeMillis();
  private var drown = 0;

/*
  def startDraw() {
      if (drown == 0){
        time = ;
      }
  }
*/

  def getFPSDrawable(x: Int, y: Int): Drawable = {
    new Drawable {
      def draw() {
        FontManager.drawText(x,y,fps+ " fps", "ArialGold");
      }
    }
  }

  def getHistogramDrawable(x: Int, y: Int): Drawable = {
    new Drawable {
      def draw() {
        drawHistogram(x,y);
      }
    }
  }

  def endDraw(){
    drown+=1 ;
      if (System.currentTimeMillis() > time+SECOND ){
        fps = drown;
        histData.enqueue(fps);
        //currentPos = if (currentPos < MAX_HIST_SIZE - 1) currentPos + 1 else 0;
        if (histData.size > MAX_HIST_SIZE) histData.dequeue();
        drown = 0;
        time = System.currentTimeMillis();
      }
  }

  def drawHistogram(drawX:Int, drawY:Int){
    glDisable(GL_TEXTURE_2D);
    glColor4f(0.3f, 0f, 0.8f,0.8f);
    glBegin(GL_QUADS);
    glVertex2i(drawX,drawY);
    glVertex2i(drawX+MAX_HIST_SIZE+4,drawY);
    glVertex2i(drawX+MAX_HIST_SIZE+4,drawY+MAX_HEG+3);
    glVertex2i(drawX,drawY+MAX_HEG+3);
    glEnd();
    //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


   glColor4f(1f,1f,0f,0.9f);
   glBegin(GL_LINES)
    var i =3;
    for (j <- histData) {
     //histData(i) = r.nextInt(MAX_HEG);
      glVertex2i(drawX+i,drawY+MAX_HEG+1);
      glVertex2i(drawX+i,drawY+MAX_HEG+1-j)
      i += 1;
    }

    glEnd();
    glEnable(GL_TEXTURE_2D);
  }


}