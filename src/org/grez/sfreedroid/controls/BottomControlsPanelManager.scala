package org.grez.sfreedroid.controls

import org.grez.sfreedroid.textures.Rect
import org.grez.sfreedroid.drawable._
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.{MapManager, DrawableEntitiesManager, DrawableEntity}
import org.grez.sfreedroid.textures.Color
import org.grez.sfreedroid.utils.RectUtils
import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.font.FontManager

import org.grez.sfreedroid.textures.Color
import org.grez.sfreedroid.DrawableEntity

import org.grez.sfreedroid.textures.Color
import org.grez.sfreedroid.DrawableEntity

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/10/12
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

private[controls] object StringConstants {

  val TOGGLE_GRID_BTN = "Toggle_Grid_btn";
  val TOGGLE_FPS_BTN = "Toggle_FPS_btn";
  val TOGGLE_MOUSE_POS_BTN = "Toggle_MousePos_btn";
  val NEXT_TILE_BTN = "Next_Tile_btn";
  val PREV_TILE_BTN = "Prev_Tile_btn";
  val BOTTOM_PANEL = "Bottom_Panel";
  val HIDE_TOOLBAR_BTN = "Hide_Toolbar_btn";
  val TILE_RECT =  "Tile_rect";
  val LOX_PLUS_BTN = "Lox_Plus_btn";
  val LOY_PLUS_BTN = "Loy_Plus_btn";
  val LOX_MINUS_BTN = "Lox_Minus_btn";
  val LOY_MINUS_BTN = "Loy_Minus_btn";
  val BP_SHOW_ANIM = "BP_show_Anim";
  val BP_HIDE_ANIM = "BP_hide_Anim";
}

class BottomControlsPanelManager {
  import StringConstants._;

  val bottom_panel = new ToolbarPanelRect(Rect((0, 565), (1024, 775)), Color(0f, 0f, 0f));
  val toggleGridBtn = new TextButton("toggle grid", 40, 635, "toggle grid");
  val toggleFpsBtn = new TextButton("toggle fps", 40, 685, "toggle fps");
  val toggleMouseBtn = new TextButton("toggle mousepos", 40, 735, "toggle mousepos");
  val nextTileBtn = new TextButton("->",890,650,"nexttile");
  val prevTileBtn = new TextButton("<-",690,650,"prevtile");
  val tileRect = new TileChooserRect;
  val loxPlusBtn = new SmallTextActionButton("+", 765, 575, (() => tileRect.inc_lox()));
  val loxMinusBtn = new SmallTextActionButton("-", 765, 600, (() => tileRect.dec_lox()));
  val loyPlusBtn = new SmallTextActionButton("+", 935, 575, (() => tileRect.inc_loy()));
  val loyMinusBtn = new SmallTextActionButton("-", 935, 600, (() => tileRect.dec_loy()));
  val hideToolbarBtnTop = new TextActionButton("^", 500, 540, hideToolbarAction);
  val hideToolbarBtnBottom = new TextActionButton("^", 500, 740, (() => addControlsPanel()));

  val animList: List[AnimDrawableSubstitute] = List(bottom_panel, toggleGridBtn.getAnimDrawableSubstitute,
    toggleFpsBtn.getAnimDrawableSubstitute, toggleMouseBtn.getAnimDrawableSubstitute,
    nextTileBtn.getAnimDrawableSubstitute, prevTileBtn.getAnimDrawableSubstitute, tileRect,
    loxPlusBtn.getAnimDrawableSubstitute, loxMinusBtn.getAnimDrawableSubstitute, loyPlusBtn.getAnimDrawableSubstitute,
    loyMinusBtn.getAnimDrawableSubstitute,
    hideToolbarBtnTop.getAnimDrawableSubstitute);


  def addControlsPanel() {
    DrawableEntitiesManager.deleteEntities(BOTTOM_PANEL, HIDE_TOOLBAR_BTN);
    DrawableEntitiesManager.addEntity(BP_SHOW_ANIM, new ControlPanelHideAnimation(false, animList, (() => {
      DrawableEntitiesManager.deleteEntity(BP_SHOW_ANIM);
      DrawableEntitiesManager.addEntities(
        DrawableEntity(BOTTOM_PANEL, bottom_panel, 2),
        DrawableEntity(TOGGLE_GRID_BTN, toggleGridBtn, 3),
        DrawableEntity(TOGGLE_FPS_BTN, toggleFpsBtn, 3),
        DrawableEntity(TOGGLE_MOUSE_POS_BTN, toggleMouseBtn, 3),
        DrawableEntity(NEXT_TILE_BTN, nextTileBtn, 3),
        DrawableEntity(PREV_TILE_BTN, prevTileBtn, 3),
        DrawableEntity(TILE_RECT,tileRect,3),
        DrawableEntity(LOX_PLUS_BTN,loxPlusBtn,3),
        DrawableEntity(LOX_MINUS_BTN,loxMinusBtn,3),
        DrawableEntity(LOY_PLUS_BTN,loyPlusBtn,3),
        DrawableEntity(LOY_MINUS_BTN,loyMinusBtn,3),
        DrawableEntity(HIDE_TOOLBAR_BTN, hideToolbarBtnTop, 3));
    })), 2)
  }

  lazy val hideToolbarAction: () => Unit = (() => {
    DrawableEntitiesManager.deleteEntities(BOTTOM_PANEL, TOGGLE_GRID_BTN, TOGGLE_FPS_BTN, TOGGLE_MOUSE_POS_BTN,
      NEXT_TILE_BTN, PREV_TILE_BTN, TILE_RECT, LOX_PLUS_BTN, LOX_MINUS_BTN, LOY_PLUS_BTN, LOY_MINUS_BTN,
      HIDE_TOOLBAR_BTN);
    DrawableEntitiesManager.addEntity(BP_HIDE_ANIM, new ControlPanelHideAnimation(true, animList, (() => {
      DrawableEntitiesManager.deleteEntity(BP_HIDE_ANIM);
      DrawableEntitiesManager.addEntity(BOTTOM_PANEL, new ToolbarPanelRect(Rect((0, 760), (1024, 768)), Color(0f, 0f, 0f)), 2);
      DrawableEntitiesManager.addEntity(HIDE_TOOLBAR_BTN, hideToolbarBtnBottom, 3);
    })), 2)
  });

}


class ControlPanelHideAnimation(val direction: Boolean, val entities: List[AnimDrawableSubstitute], override val callBack: () => Unit) extends AnimBasic {
  private val MAX_Y_OFFSET = 760 - 565;

  def toBottom = direction;

  override var stage = if (toBottom) 0 else MAX_Y_OFFSET;
  override val step = if (toBottom) 10 else -10;
  override val checkLbd: (Int) => Boolean = if (toBottom) ((i: Int) => i >= MAX_Y_OFFSET) else ((i: Int) => i <= 0)

  def drw() {
    entities.foreach(entity => entity.draw(0, stage))
  }
}


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

class ToolbarPanelRect(override val rect: Rect, color: Color) extends Control with TranslateAnim {

  def draw() {
    import color._

    glDisable(GL_TEXTURE_2D);
    glColor3f(red, green, blue);
    glBegin(GL_QUADS);
    import rect._

    glVertex2i(leftTop.getX, leftTop.getY);
    glVertex2i(rightTop.getX, rightTop.getY);
    glVertex2i(rightBottom.getX, rightBottom.getY);
    glVertex2i(leftBottom.getX, leftBottom.getY);
    glEnd();
    glEnable(GL_TEXTURE_2D);
  }

  private var mouseX: Int = 0;
  private var mouseY: Int = 0;

  override def checkMouseOn(x: Int, y: Int) = {
      mouseX = x;
      mouseY = y;
     super.checkMouseOn(x, y)
  }


  def mouseDown() {
    DrawableEntitiesManager.addEntity("Click_Anim", new ClickAnim(mouseX,mouseY,Color(0.8f,0.2f,0.0f),(() => DrawableEntitiesManager.deleteEntity("Click_Anim"))),3);
  }

  def mouseUp() {}
}

