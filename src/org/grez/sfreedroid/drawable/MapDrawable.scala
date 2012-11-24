package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.{utils, MapManager}
import utils.{RectUtils, MouseGridHelper}
import utils.NumberUtils._
import org.grez.sfreedroid.MapDefaults._
import org.grez.sfreedroid.controls.{Control, OnMousePosUpdate}
import org.grez.sfreedroid.textures.Rect
import org.lwjgl.opengl.GL11._

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
class MapDrawable extends Control with Drawable with OnMousePosUpdate {

  import GlobalDebugState.selectedMapTile;

  private val mouseHelper: MouseGridHelper = new MouseGridHelper;

  override val rect = Rect((0, 0), (1024, 768));

  private def draw(x: Float, y: Float, id: Int) {
    val mt = MapManager.allTestData(id);
    mt.tx.draw(x, y);
    //mt.tx.draw(x+mt.offsetX*0.5f,y+mt.offsetY*0.5f);
  }

  def draw() {
    import GlobalDebugState._;


    val flatCursorX: Int = cursorMovementX.toInt / DEF_WIDTH;
    val flatCursorY: Int = (cursorMovementY.toInt / DEF_HEIGHT);
    val relativeY = flatCursorY * 2;

    if (relativeY >= SIZE_Y || flatCursorX >= SIZE_X) return; //no drawing if out of map.

    val startY = relativeY;
    val endY = if ((startY + 26) > SIZE_Y) SIZE_Y else startY + 26;
    val startX = flatCursorX;
    val endX = if ((startX + 10) > SIZE_X) SIZE_X else startX + 10;

    var curY = 0;
    var curX = 0;

    glPushMatrix();
    glTranslatef(-cursorMovementX, -cursorMovementY, 0f);
    glColor4f(0f, 0f, 0f, 1.0f)
    for {y <- startY until endY; x <- startX until endX} {

      if (isOdd(y)) {
        curX = DEF_WIDTH * x;
        curY = DEF_HEIGHT / 2 * (y - 1);
      } else {
        curX = (DEF_WIDTH * x) - (DEF_WIDTH / 2);
        curY = (DEF_HEIGHT / 2 * y) - (DEF_HEIGHT / 2);
      }
      val tileId = MapManager.mapa(x)(y);
      val (ox, oy) = MapManager.tileOffsets(tileId).getOrElse((0, 0));

      draw(curX + ox, curY + oy, tileId)

    }

    drawSelected();
    glPopMatrix();
  }

  def drawSelected() {
    if (selectedMapTile.isDefined) {
      val (x, y) = selectedMapTile.get;
      val r = RectUtils.getMapTileRect(x, y);

      glDisable(GL_TEXTURE_2D)
      glColor3f(0.2f, 0.5f, 1.0f);
      glBegin(GL_LINES);
      r.directDrw();
      glEnd();
      glEnable(GL_TEXTURE_2D);

    }
  }

  override def updateMousePos(x: Int, y: Int) {
    super.updateMousePos(x, y);
    mouseHelper.updateMousePos(x, y);

  }


  def mouseDown() {
    import mouseHelper.{selectedX => x, selectedY => y};
    if (MapManager.coorsWithinMap(x, y)) {
      selectedMapTile = Option((x, y));
    } else {
      selectedMapTile = None;
    }
  }

  def mouseUp() {

  }

  lazy val getGridDrawable = new MapGridDrawable(mouseHelper);
  lazy val getGridDebugDrawable = new MouseGridDebugDrawable(mouseHelper);
  lazy val getMousePosDrawable = new MousePosDrawable(mouseHelper)
}

private[drawable] class MapGridDrawable(val mouseHelper: MouseGridHelper) extends Drawable {
  def draw() {
    import mouseHelper._
    drawGrid(selectedX, selectedY, flatCordMapX, flatCordMapY);
  }

  def drawGrid(selectedX: Int, selectedY: Int, flatX: Int, flatY: Int) {
    glPushMatrix();
    glTranslatef(-GlobalDebugState.cursorMovementX, -GlobalDebugState.cursorMovementY, 0f);
    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LINE_SMOOTH);
    glColor3f(1.0f, 0f, 1.0f);
    glBegin(GL_LINES);
    val wdt = SIZE_X * DEF_WIDTH;
    val hgt = SIZE_Y * DEF_HEIGHT/2;
    for (i <- 0 to SIZE_X) {
      val curX = i * DEF_WIDTH;
      glVertex2i(curX, 0);
      glVertex2i(curX, hgt);
    }
    for (j <- 0 to SIZE_Y/2) {
      val curY = j * DEF_HEIGHT;
      glVertex2i(0, curY);
      glVertex2i(wdt, curY);
    }

    glColor3f(0.0f, 0.0f, 0.0f);

    for (i <- 0 to SIZE_X) {
      val curX = wdt - (i * DEF_WIDTH + HALF_DEF_WIDTH);
      val curY = (i * DEF_HEIGHT + HALF_DEF_HEIGHT);
      glVertex2i(curX, 0);
      glVertex2i(wdt, curY);
    }

    for (j <- 0 to SIZE_Y) {
      val curX = j * DEF_WIDTH + HALF_DEF_WIDTH;
      val curY = j * DEF_HEIGHT + HALF_DEF_HEIGHT;
      glVertex2i(0, curY);
      glVertex2i(wdt-curX, hgt);
    }


    for (j <- SIZE_X to (0, -1)) {
      val curX = j * DEF_WIDTH + HALF_DEF_WIDTH;
      val curY = j * DEF_HEIGHT + HALF_DEF_HEIGHT;
      glVertex2i(curX, 0);
      glVertex2i(0, curY);
    }

    for (j <- 0 to SIZE_X) {
      val curX = j * DEF_WIDTH + HALF_DEF_WIDTH;
      val curY = j * DEF_HEIGHT + HALF_DEF_HEIGHT;
      glVertex2i(curX, hgt);
      glVertex2i(wdt, curY);
    }



    glColor3f(1.0f, 1.0f, 0.0f);
    RectUtils.getMapRect(flatX, flatY).directDrw();
    glColor3f(1f, 1f, 1f);
    RectUtils.getMapTileRect(selectedX, selectedY).directDrw();

    glEnd();
    glFlush();
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_LINE_SMOOTH);
    glPopMatrix();
  }
}
