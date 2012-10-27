package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.{utils, MapManager}
import utils.MouseGridHelper
import utils.NumberUtils._
import org.grez.sfreedroid.MapDefaults._

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
class MapDrawable extends Drawable {
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
  }
}

class MapGridDrawable(val mouseHelper: MouseGridHelper) extends Drawable {
  def draw() {
    if (GlobalDebugState.DrawGridFlag){
      import mouseHelper._
       MapManager.drawGrid(selectedX,selectedY,flatCordMapX,flatCordMapY);
    }
  }
}
