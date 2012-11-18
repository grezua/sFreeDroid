package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.{utils, MapManager}
import utils.MouseGridHelper
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

  override val rect = Rect((0,0),(1024,768));

  private def draw(x: Float,y: Float,id: Int) {
     val mt = MapManager.allTestData(id);
         mt.tx.draw(x,y);
         //mt.tx.draw(x+mt.offsetX*0.5f,y+mt.offsetY*0.5f);
   }

  def draw() {
    var curY = 0;
    var curX = 0;

    for {y <-0 until SIZE_Y; x <-0 until SIZE_X  }{

    if (isOdd(y)){
        curX = DEF_WIDTH * x;
        curY = DEF_HEIGHT /2 * (y - 1);
    } else{
      curX = (DEF_WIDTH * x) - (DEF_WIDTH / 2);
      curY = (DEF_HEIGHT /2 * y) - (DEF_HEIGHT / 2);
    }
      draw(curX,curY,MapManager.mapa(x)(y))

    }
    drawSelected();
  }

  def drawSelected(){
    if (selectedMapTile.isDefined){
      val (x,y) = selectedMapTile.get;
      val r = MapManager.getTileRect(x,y);

      glDisable(GL_TEXTURE_2D)
      glColor3f(0.2f, 0.5f, 1.0f);
      glShadeModel(GL_FLAT);
      glBegin(GL_LINES);
       r.directDrw();
      glEnd();
      glEnable(GL_TEXTURE_2D);

    }
  }

  override def updateMousePos(x: Int, y: Int) {
    super.updateMousePos(x, y);
    mouseHelper.updateMousePos(x,y);

  }


  def mouseDown() {
    import  mouseHelper.{selectedX => x,selectedY => y};
    if (MapManager.coorsWithinMap(x,y)) {
      selectedMapTile = Option((x,y));
    } else {
      selectedMapTile = None;
    }
  }

  def mouseUp() {

  }

  def mapaClck(){
    //if (MapManager.mapa(x)(y) >= NUM_OF_IDS) MapManager.mapa(x)(y) = 0 else MapManager.mapa(x)(y) +=1;
  }

  lazy val getGridDrawable = new MapGridDrawable(mouseHelper);
  lazy val getGridDebugDrawable = new MouseGridDebugDrawable(mouseHelper);
  lazy val getMousePosDrawable = new MousePosDrawable(mouseHelper)

/*  def mouseDown() {}

  def mouseUp() {}

  val rect = Rect((0,0),(1024,768))*/


}

private[drawable] class MapGridDrawable(val mouseHelper: MouseGridHelper) extends Drawable {
  def draw() {
       import mouseHelper._

       MapManager.drawGrid(selectedX,selectedY,flatCordMapX,flatCordMapY);
  }
}
