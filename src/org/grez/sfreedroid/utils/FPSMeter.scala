package org.grez.sfreedroid.utils

import org.lwjgl.opengl.GL11._
import java.util.Random

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/2/11
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */

class FPSMeter()  {
  private val MAX_HIST_SIZE = 150;
  private val MAX_HEG = 100;
  private val SECOND = 1000L;

  val histData: Array[Int] = new Array[Int](MAX_HIST_SIZE);
  val r: Random = new Random();
  for (i <- 0 until  MAX_HIST_SIZE) {
    histData(i) = r.nextInt(MAX_HEG);
  }

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

  def endDraw(){
    drown+=1 ;
      if (System.currentTimeMillis() > time+SECOND ){
        fps = drown;
        drown = 0;
        time = System.currentTimeMillis();
      }
  }

  def drawHistogram(drawX:Int, drawY:Int){
    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
    for (i <- 0 until  MAX_HIST_SIZE) {
     //histData(i) = r.nextInt(MAX_HEG);
      glVertex2i(drawX+2+i,drawY+MAX_HEG+1);
      glVertex2i(drawX+2+i,drawY+MAX_HEG+1-histData(i))
    }

    glEnd();
     glDisable(GL_BLEND);
    glEnable(GL_TEXTURE_2D);
  }

}