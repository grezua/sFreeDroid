package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.utils.MouseGridHelper
import org.grez.sfreedroid.controls.OnMousePosUpdate

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 10:42 PM
 * Draw various cords of mouse for debug purpose;
 */
private[drawable] class MouseGridDebugDrawable(val mouseGridHelper: MouseGridHelper) extends Drawable  {

  def draw() {
    import mouseGridHelper._

    FontManager.drawText(800, 30, "mouseX=" + mouseX, "ArialGold");
    FontManager.drawText(800, 60, "mouseY=" + mouseY, "ArialGold");
    FontManager.drawText(800, 90, "local: [" + localMX + "," + localMY + "]", "ArialGold");
    FontManager.drawText(800, 120, "flat: [" + flatCordMapX + "," + flatCordMapY + "]", "ArialGold");
    FontManager.drawText(800, 150, "selected: [" + selectedX + "," + selectedY + "]", "ArialGold");
    FontManager.drawText(700, 170, "localSector: [" + transformed.xt + "," + transformed.yt + "," + transformed.cxOffset + ","
      + transformed.cyOffset + "," + X_to_Y_triCoords(transformed.xt) + "]", "redfont");
    FontManager.drawText(700, 190, "transformed: [" + selected._1 + "," + selected._2 + "]", "redfont");

  }


}




