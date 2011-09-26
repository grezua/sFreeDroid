package org.grez.sfreedroid.textures

import org.lwjgl.util.Point

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */

case class Rect (leftTop:Point, rightTop:Point, rightBottom:Point, leftBottom:Point){
  private def pointToString(p:Point) = "{x="+p.getX+",y="+p.getY+"}"

  override def toString = "lt"+pointToString(leftTop)+" rt"+pointToString(rightTop)+"rb"+pointToString(rightBottom)+"lb"+pointToString(leftBottom);
}