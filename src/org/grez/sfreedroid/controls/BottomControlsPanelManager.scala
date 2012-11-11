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
   def addControlsPanel () {
    DrawableEntitiesManager.addEntity("Bottom_Panel", new SimpleRect(Rect((0,565),(1024,768)),Color(0f,0f,0f)),2);
    DrawableEntitiesManager.addEntity("Togle_Grid_btn", new TextButton("toggle grid", 40,635, "toggle grid"),3);
    DrawableEntitiesManager.addEntity("Togle_FPS_btn", new TextButton("toggle fps", 40,685, "toggle fps"),3);
    DrawableEntitiesManager.addEntity("Togle_MousePos_btn", new TextButton("toggle mousepos", 40,735, "toggle mousepos"),3);
    DrawableEntitiesManager.addEntity("Hide_Toolbar_btn" , new TextActionButton("^",500,540,hideToolbarAction), 3);
  }

  val hideToolbarAction: () => Unit = (()=> {
    DrawableEntitiesManager.deleteEntries("Bottom_Panel", "Togle_Grid_btn", "Togle_FPS_btn", "Togle_MousePos_btn", "Hide_Toolbar_btn");
    DrawableEntitiesManager.addEntity("Bottom_Panel", new SimpleRect(Rect((0,760),(1024,768)),Color(0f,0f,0f)),2);
    DrawableEntitiesManager.addEntity("Hide_Toolbar_btn" , new TextActionButton("^",500,740,(() => addControlsPanel())), 3);
  })
}


class SimpleRect(val rect: Rect, color: Color) extends  Drawable{

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
}

