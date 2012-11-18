package org.grez.sfreedroid.textures

import org.lwjgl.util.Point
import org.lwjgl.opengl.GL11._

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */

object Rect {
  private def P(x:Int, y: Int) = new Point(x,y);

  def apply (ltx: Int, lty: Int, rtx: Int, rty: Int, rbx: Int, rby: Int, lbx: Int, lby: Int) = {
    new Rect(P(ltx,lty),P(rtx,rty), P(rbx,rby), P(lbx,lby) );
  }

  def apply(ltp: (Int,Int), rbp: (Int, Int)) = {
    val (ltx,lty) = ltp;
    val (rbx,rby) = rbp;

    new Rect(P(ltx,lty),P(rbx,lty),P(rbx,rby),P(ltx,rby));
  }
}

case class Rect (leftTop:Point, rightTop:Point, rightBottom:Point, leftBottom:Point){

  def isCordsWithin(x: Int, y: Int): Boolean = {
    x>=leftTop.getX && x<=rightBottom.getX && y >= leftTop.getY && y <= rightBottom.getY
  }

  private def pointToString(p:Point) = "{x="+p.getX+",y="+p.getY+"}"

  override def toString = "lt"+pointToString(leftTop)+" rt"+pointToString(rightTop)+"rb"+pointToString(rightBottom)+"lb"+pointToString(leftBottom);

  def directDrw() {
        glVertex2i(leftTop.getX,     leftTop.getY);
        glVertex2i(rightTop.getX,    rightTop.getY);

        glVertex2i(rightTop.getX,    rightTop.getY);
        glVertex2i(rightBottom.getX, rightBottom.getY);

        glVertex2i(rightBottom.getX, rightBottom.getY);
        glVertex2i(leftBottom.getX,  leftBottom.getY);

        glVertex2i(leftBottom.getX,  leftBottom.getY);
        glVertex2i(leftTop.getX,     leftTop.getY);
  }
}