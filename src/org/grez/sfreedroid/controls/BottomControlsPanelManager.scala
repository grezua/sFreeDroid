package org.grez.sfreedroid.controls

import org.grez.sfreedroid.textures.{Color, Rect}
import org.grez.sfreedroid.drawable.Drawable
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.DrawableEntitiesManager


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/10/12
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

class BottomControlsPanelManager {
  val bottom_panel = new SimpleRect(Rect((0, 565), (1024, 775)), Color(0f, 0f, 0f));
  val toggleGridBtn = new TextButton("toggle grid", 40, 635, "toggle grid");
  val toggleFpsBtn = new TextButton("toggle fps", 40, 685, "toggle fps");
  val toggleMouseBtn = new TextButton("toggle mousepos", 40, 735, "toggle mousepos");
  val hideToolbarBtn = new TextActionButton("^", 500, 540, hideToolbarAction);

  val animList: List[AnimDrawableSubstitute] = List(bottom_panel, toggleGridBtn.getAnimDrawableSubstitute,
    toggleFpsBtn.getAnimDrawableSubstitute, toggleMouseBtn.getAnimDrawableSubstitute, hideToolbarBtn.getAnimDrawableSubstitute);

  def addControlsPanel() {
    DrawableEntitiesManager.deleteEntries("Bottom_Panel", "Hide_Toolbar_btn");
    DrawableEntitiesManager.addEntity("BP_show_Anim", new ControlPanelHideAnimation(false, animList, (() => {
      DrawableEntitiesManager.deleteEntries("BP_show_Anim");
      DrawableEntitiesManager.addEntity("Bottom_Panel", bottom_panel, 2);
      DrawableEntitiesManager.addEntity("Togle_Grid_btn", toggleGridBtn, 3);
      DrawableEntitiesManager.addEntity("Togle_FPS_btn", toggleFpsBtn, 3);
      DrawableEntitiesManager.addEntity("Togle_MousePos_btn", toggleMouseBtn, 3);
      DrawableEntitiesManager.addEntity("Hide_Toolbar_btn", hideToolbarBtn, 3);
    })), 2)
  }

  lazy val hideToolbarAction: () => Unit = (() => {
    DrawableEntitiesManager.deleteEntries("Bottom_Panel", "Togle_Grid_btn", "Togle_FPS_btn", "Togle_MousePos_btn", "Hide_Toolbar_btn");
    DrawableEntitiesManager.addEntity("BP_hide_Anim", new ControlPanelHideAnimation(true, animList, (() => {
      DrawableEntitiesManager.deleteEntry("BP_hide_Anim");
      DrawableEntitiesManager.addEntity("Bottom_Panel", new SimpleRect(Rect((0, 760), (1024, 768)), Color(0f, 0f, 0f)), 2);
      DrawableEntitiesManager.addEntity("Hide_Toolbar_btn", new TextActionButton("^", 500, 740, (() => addControlsPanel())), 3);
    })), 2)

  })

}

class ControlPanelHideAnimation(val direction: Boolean, val entities: List[AnimDrawableSubstitute], val callBack: () => Unit) extends Drawable {
  private val MAX_Y_OFFSET = 760 - 565;
  private var stop = false;

  def toBottom = direction;
  //true means we start from top and go to bottom, false - otherwise.

  private var stage = if (toBottom) 0 else MAX_Y_OFFSET;
  private val step = if (toBottom) 10 else -10;
  private val checkLbd: (Int) => Boolean = if (toBottom) ((i: Int) => i >= MAX_Y_OFFSET) else ((i: Int) => i <= 0)

  def draw() {
    d();

    if (checkLbd(stage) && !stop) {
      callBack();
      stop = true;
    } else {
      stage += step;
    }
  }

  def d() {
    entities.foreach(entity => entity.draw(0, stage))
  }
}


class SimpleRect(val rect: Rect, color: Color) extends Drawable with AnimDrawableSubstitute {

  def draw() {
    import color._

    glShadeModel(GL_FLAT);
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

  def draw(offsetX: Float, offsetY: Float) {
    glPushMatrix();
    glTranslatef(offsetX, offsetY, 0);
    draw();
    glPopMatrix();
  }
}

