package org.grez.controls

import org.grez.sfreedroid.textures.Rect
import org.grez.sfreedroid.drawable.OnMousePosUpdate

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/3/12
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
/*trait Mouseable extends OnMousePosUpdate{


  //def inputMouseCords(x: Int, y: Int);
}*/

abstract class RectMouseable extends OnMousePosUpdate {
  val rect: Rect;

  protected var isMouseOn_ = false;
  def isMouseOn = isMouseOn_;

  override def updateMousePos(x: Int, y: Int) {
    super.updateMousePos(x, y);

    if (rect.isCordsWithin(mouseX,mouseY))
          isMouseOn_ = true;
        else
          isMouseOn_ = false;
  }

}
