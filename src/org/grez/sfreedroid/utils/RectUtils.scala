package org.grez.sfreedroid.utils

import org.grez.sfreedroid.textures.Rect
import org.lwjgl.util.Point
import org.grez.sfreedroid.MapDefaults

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/18/12
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
object RectUtils {
  import MapDefaults._
  def getMapRect(x: Int, y: Int): Rect = {

    val lx = x * DEF_WIDTH;
    val rx = lx + DEF_WIDTH;
    val ty = y * DEF_HEIGHT;
    val by = ty + DEF_HEIGHT;

    Rect((lx,ty),(rx,by));
  }

  def getMapTileRect(x: Int, y: Int): Rect = {
    import NumberUtils.isOdd;
    val pt = (zx: Int, zy: Int) => new Point(zx, zy);


    val lx = (x * DEF_WIDTH) -  {if (!isOdd(y)) HALF_DEF_WIDTH else 0};
    val mx = (lx + HALF_DEF_WIDTH)
    val rx = (lx + DEF_WIDTH)
    val ty = (y * HALF_DEF_HEIGHT) - HALF_DEF_HEIGHT
    val my = (ty + HALF_DEF_HEIGHT)
    val by = (ty + DEF_HEIGHT)


    Rect(leftTop = pt(lx, my), rightTop = pt(mx, ty), rightBottom = pt(rx, my), leftBottom = pt(mx, by));
  }

}
