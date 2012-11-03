package org.grez.sfreedroid.utils

import org.grez.sfreedroid.drawable.OnMousePosUpdate;
import org.grez.sfreedroid.MapDefaults._;

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
class MouseGridHelper extends OnMousePosUpdate {


  case class TriangleHelper(xt: Int, yt: Int, cxOffset: Int, cyOffset: Int);


  var my: Int = 0;
  var mx: Int = 0;

  var flatCordMapX: Int = 0;
  var flatCordMapY: Int = 0;


  var localMX: Int = 0;
  var localMY: Int = 0;

  var transformed: TriangleHelper = null;

  var selectedX: Int = 0;
  var selectedY: Int = 0;

  var selected: (Int, Int) = null;

  override def updateMousePos(x: Int, y: Int) {
    super.updateMousePos(x, y);

    my = mouseY //767 - mouseY;
    mx = mouseX;

    flatCordMapX = mx / DEF_WIDTH;
    flatCordMapY = my / DEF_HEIGHT;

    localMX = mx % DEF_WIDTH;
    localMY = my % DEF_HEIGHT;

    transformed = transformCoords(localMX, localMY)

    selected = findOutLocalSelected(transformed);

    selectedX = flatCordMapX + selected._1;
    selectedY = (flatCordMapY * 2) + selected._2 + 1;
  }

   /*private*/ lazy val X_to_Y_triCoords: List[Int] = {
      /* ok, Lets assume following: ABC - our triangle
      A is point in right angle (0,0) ;B - x-axis point (wdt,0); C is y-axis point (0,heg)
      Now we need to find out all y coordinates that correspond to each x coordinate (that lie on hypotenuse)
      AC/AB = tang<B;
      Y/AX = tang<B;
      Y = AX*tang<B;
       */
      val tang: Float = HALF_DEF_HEIGHT.toFloat / HALF_DEF_WIDTH.toFloat;
      //      DefaultConsole.log("tang ="+ tang);
      (for (x <- HALF_DEF_WIDTH to(0, -1)) yield (x * tang).round).toList
      //list is reversed, because our coordinate system differs a little.
    }

  private def transformCoords(x: Int, y: Int): TriangleHelper = {

    val xt = if (x > HALF_DEF_WIDTH) (DEF_WIDTH - x, 1) else (x, 0);
    val yt = if (y > HALF_DEF_HEIGHT) (DEF_HEIGHT - y, 1) else (y, -1);
    //center romb is 0,1 // up left 0,0// up rigth 1,0// bottom left 0,2// bottom rigth 1,2

    TriangleHelper(xt._1, yt._1, xt._2, yt._2);
  }

  private def inTriangle(x: Int, y: Int): Boolean = X_to_Y_triCoords(x) >= y

  private def findOutLocalSelected(th: TriangleHelper): (Int, Int) = {
    if (inTriangle(th.xt, th.yt)) (th.cxOffset, th.cyOffset) else (0, 0);
  }
}
