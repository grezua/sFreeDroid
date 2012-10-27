package org.grez.sfreedroid.drawable

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
class TextDrawable(val text: String, val x: Int, val y: Int, val fontName: String = "redfont") extends Drawable{
  import org.grez.sfreedroid.font.FontManager._

  def draw() {
    drawText(x=x, y=y, str=text, fontName=fontName)
  }
}
