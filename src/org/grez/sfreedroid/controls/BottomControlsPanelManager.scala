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
import collection.immutable

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
  val toggleDbgButtonsPanel: ControlsPanel = new ControlsPanel;

  toggleDbgButtonsPanel.addItem(TOGGLE_GRID_BTN,new TextButton("toggle grid", 40, 635, "toggle grid"),3);
  toggleDbgButtonsPanel.addItem(TOGGLE_FPS_BTN,new TextButton("toggle fps", 40, 685, "toggle fps"),3 )
  toggleDbgButtonsPanel.addItem(TOGGLE_MOUSE_POS_BTN,new TextButton("toggle mousepos", 40, 735, "toggle mousepos"),3)

  val tileControlsPanel = TileControlPanel;

  val hideToolbarBtnTop = new TextActionButton("^", 500, 540, hideToolbarAction);
  val hideToolbarBtnBottom = new TextActionButton("^", 500, 740, addControlsPanel);


  def animList: List[AnimDrawableSubstitute] = List(bottom_panel, hideToolbarBtnTop.getAnimDrawableSubstitute) ++
    toggleDbgButtonsPanel.getAnimList ++ tileControlsPanel.getAnimList;


  def addControlsPanel() {
    DrawableEntitiesManager.deleteEntities(BOTTOM_PANEL, HIDE_TOOLBAR_BTN);
    DrawableEntitiesManager.addEntity(BP_SHOW_ANIM, new ControlPanelHideAnimation(false, animList, (() => {
      DrawableEntitiesManager.deleteEntity(BP_SHOW_ANIM);
      DrawableEntitiesManager.addEntities(
        DrawableEntity(BOTTOM_PANEL, bottom_panel, 2),
        DrawableEntity(HIDE_TOOLBAR_BTN, hideToolbarBtnTop, 3));
      DrawableEntitiesManager.addEntities(toggleDbgButtonsPanel.getEntries ++ tileControlsPanel.getEntries :_*);
    })), 2)
  }

  def hideToolbarAction()  {
    DrawableEntitiesManager.deleteEntities(BOTTOM_PANEL, HIDE_TOOLBAR_BTN);
    DrawableEntitiesManager.deleteEntities(toggleDbgButtonsPanel.getEntriesNames ++ tileControlsPanel.getEntriesNames :_*);
    DrawableEntitiesManager.addEntity(BP_HIDE_ANIM, new ControlPanelHideAnimation(true, animList, (() => {
      DrawableEntitiesManager.deleteEntity(BP_HIDE_ANIM);
      DrawableEntitiesManager.addEntity(BOTTOM_PANEL, new ToolbarPanelRect(Rect((0, 760), (1024, 768)), Color(0f, 0f, 0f)), 2);
      DrawableEntitiesManager.addEntity(HIDE_TOOLBAR_BTN, hideToolbarBtnBottom, 3);
    })), 2)
  };

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

