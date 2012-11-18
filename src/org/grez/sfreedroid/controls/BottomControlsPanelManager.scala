package org.grez.sfreedroid.controls

import org.grez.sfreedroid.textures.{Color, Rect}
import org.grez.sfreedroid.drawable.{AnimBasic, AnimDrawableSubstitute, Drawable}
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.{DrawableEntity, DrawableEntitiesManager}

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
    DrawableEntitiesManager.deleteEntities("Bottom_Panel", "Hide_Toolbar_btn");
    DrawableEntitiesManager.addEntity("BP_show_Anim", new ControlPanelHideAnimation(false, animList, (() => {
      DrawableEntitiesManager.deleteEntity("BP_show_Anim");
      DrawableEntitiesManager.addEntities(
        DrawableEntity("Bottom_Panel", bottom_panel, 2),
        DrawableEntity("Togle_Grid_btn", toggleGridBtn, 3),
        DrawableEntity("Togle_FPS_btn", toggleFpsBtn, 3),
        DrawableEntity("Togle_MousePos_btn", toggleMouseBtn, 3),
        DrawableEntity("Hide_Toolbar_btn", hideToolbarBtn, 3));
    })), 2)
  }

  lazy val hideToolbarAction: () => Unit = (() => {
    DrawableEntitiesManager.deleteEntities("Bottom_Panel", "Togle_Grid_btn", "Togle_FPS_btn", "Togle_MousePos_btn", "Hide_Toolbar_btn");
    DrawableEntitiesManager.addEntity("BP_hide_Anim", new ControlPanelHideAnimation(true, animList, (() => {
      DrawableEntitiesManager.deleteEntity("BP_hide_Anim");
      DrawableEntitiesManager.addEntity("Bottom_Panel", new SimpleRect(Rect((0, 760), (1024, 768)), Color(0f, 0f, 0f)), 2);
      DrawableEntitiesManager.addEntity("Hide_Toolbar_btn", new TextActionButton("^", 500, 740, (() => addControlsPanel())), 3);
    })), 2)

  })

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


class ClickAnim(val x: Int, val y: Int, val color: Color, override val callBack: () => Unit) extends AnimBasic {

  private val MAX_STEP = 50;
  override var stage = 0;
  override val step = 1;

  override val checkLbd: (Int) => Boolean =  ((i: Int) => i >= MAX_STEP);


  def drw() {
    import color._

    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    glColor3f(red, green, blue);

    glPushMatrix();
    glTranslatef(x,y,0.0f)
    glRotatef(stage  * 5, 0.0f,0.0f,1.0f);

    glBegin(GL_LINES);
     glVertex2i(-10,10);
     glVertex2i(+10,-10);

     glVertex2i(-10,-10);
     glVertex2i(+10,+10);

    glEnd();
    glPopMatrix();
    glEnable(GL_TEXTURE_2D);
  }

}

class SimpleRect(override val rect: Rect, color: Color) extends Control with Drawable with AnimDrawableSubstitute {

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

