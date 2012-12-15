package org.grez.sfreedroid.controls

import org.grez.sfreedroid.textures.Rect

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/9/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
trait OnMousePosUpdate {
  private var mouseX_ : Int = 0;
  def mouseX = mouseX_

  private var mouseY_ : Int = 0;
  def mouseY = mouseY_

   def updateMousePos(x: Int, y: Int) {
     this.mouseX_ = x;
     this.mouseY_ = y; //767 - y;
   }
}

abstract class Control  {
  val rect: Rect;

  def mouseDown();
  def mouseUp();

  private var _isMouseOn = false;

  def isMouseOn_= (v: Boolean){
    _isMouseOn = v;
  }

  def isMouseOn = _isMouseOn;

  def checkMouseOn(x: Int, y: Int): Boolean = {
    (rect.isCordsWithin(x,y));
  }

}

