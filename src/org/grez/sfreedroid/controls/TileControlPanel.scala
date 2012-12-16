package org.grez.sfreedroid.controls

import org.grez.sfreedroid.drawable.TranslateAnim
import org.grez.sfreedroid.utils.RectUtils
import org.grez.sfreedroid.textures.Color
import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.font.FontManager._
import org.grez.sfreedroid.textures.Color
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.textures.Color
import org.grez.sfreedroid.DrawableEntity

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 12/15/12
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */

class TileChooserRect extends TranslateAnim {
  import org.grez.sfreedroid.MapManager._
  import org.grez.sfreedroid.MapDefaults._

  val tileRect = RectUtils.getMapTileRect(6, 20);
  val tileDrawX = (DEF_WIDTH * 6) - (DEF_WIDTH / 2);
  val tileDrawY = (DEF_HEIGHT /2 * 20) - (DEF_HEIGHT / 2);
  val color = Color(0.2f, 0.5f, 1.0f);

  private def selectedTransformFunction(lbd: (Int, Int) => (Int, Int)) {
    getSelectedTileId.foreach(tileId => {
      val (ox, oy) = tileOffsets(tileId).getOrElse((0, 0))
      tileOffsets(tileId) = Option(lbd(ox, oy));
    });
  }

  def inc_lox(){
    selectedTransformFunction((ox,oy)=>(ox+1,oy));
  }

  def dec_lox(){
    selectedTransformFunction((ox,oy)=>(ox-1,oy));
  }

  def inc_loy(){
    selectedTransformFunction((ox,oy)=>(ox,oy+1));
  }

  def dec_loy() {
    selectedTransformFunction((ox,oy)=>(ox,oy-1));
  }

  def getSelectedTileId: Option[Int] = {
    val selected = GlobalDebugState.selectedMapTile;
    if (selected.isDefined) {
      val (sx, sy) = selected.get;
      Option(mapa(sx)(sy))
    } else None;
  }

  def draw() {
    import color._;
    import FontManager._;

    val selected = GlobalDebugState.selectedMapTile;
    if (selected.isDefined){
      val (sx,sy) = selected.get;
      val id = mapa(sx)(sy);
      val tile = allTestData(id);
      val (lox, loy) = tileOffsets(id).getOrElse((0,0));

      tile.tx.draw(tileDrawX + lox, tileDrawY + loy);
      val txt = "["+sx+","+sy+"] id="+id+"; offX="+tile.offsetX+"; offY="+tile.offsetY;
      drawText(680,710,txt,RED_FONT);
      drawText(680,600,"lox="+lox,RED_FONT);
      drawText(850,600,"loy="+loy,RED_FONT);
    }

    glDisable(GL_TEXTURE_2D);
    glColor3f(red, green, blue);
    glBegin(GL_LINES);
    tileRect.directDrw();
    glEnd();
    glEnable(GL_TEXTURE_2D);
  }
}

object TileControlPanel extends ControlsPanel {
  import StringConstants._;

  val tileRect = new TileChooserRect;


  items = List(
    DrawableEntity(NEXT_TILE_BTN,  TextCMDButton("->", 890, 650, "nexttile"), 3),
    DrawableEntity(PREV_TILE_BTN,  TextCMDButton("<-", 690, 650, "prevtile"), 3),
    DrawableEntity(TILE_RECT, tileRect, 3),
    DrawableEntity(LOX_PLUS_BTN, new SmallTextActionButton("+", 765, 575, (() => tileRect.inc_lox())), 3),
    DrawableEntity(LOX_MINUS_BTN, new SmallTextActionButton("-", 765, 600, (() => tileRect.dec_lox())), 3),
    DrawableEntity(LOY_PLUS_BTN, new SmallTextActionButton("+", 935, 575, (() => tileRect.inc_loy())), 3),
    DrawableEntity(LOY_MINUS_BTN, new SmallTextActionButton("-", 935, 600, (() => tileRect.dec_loy())), 3));
}
