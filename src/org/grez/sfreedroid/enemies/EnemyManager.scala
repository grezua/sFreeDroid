package org.grez.sfreedroid.enemies

import util.Random
import org.grez.sfreedroid.MapDefaults
import org.grez.sfreedroid.textures.Rect
import org.lwjgl.opengl.GL11
import GL11._
import org.grez.sfreedroid.drawable.Drawable
import org.grez.sfreedroid.debug.GlobalDebugState
import org.grez.sfreedroid.utils.RobotLoader
import org.grez.sfreedroid.utils.NumberUtils._
import org.grez.sfreedroid.MapDefaults._

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 12/1/12
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */

class EnemyEntry(var mapPosX: Float, var mapPosY: Float, var a: Int, val rotateAnim: Boolean, val enemyType: Int = 1, var health: Int = 5 ){
  override def toString = "[x ="+mapPosX+"; y= "+ mapPosY+"; a="+a+"]"
}

object EnemyEntry {
  def apply (mapPosX: Float, mapPosY: Float, a: Int, rotateAnim: Boolean) = new EnemyEntry(mapPosX,mapPosY, a, rotateAnim);
  def apply (mapPosX: Float, mapPosY: Float, a: Int) = new EnemyEntry(mapPosX,mapPosY, a, false);
}

object EnemyLoadManager {
  val loader = new RobotLoader();
  val robot139 = loader.loadRobot("./graphics/droids/139/139.tux_image_archive.z");
  val male = loader.loadRobot("./graphics/droids/default_male/default_male.tux_image_archive.z")
}

object EnemyManager {
  import EnemyLoadManager._;

  var allEnemies: List[EnemyEntry] = List(EnemyEntry(0f,3f, 0), EnemyEntry(1f,4f, 1),EnemyEntry(1f,5f, 2),
    EnemyEntry(2f,6f, 3),EnemyEntry(2f,7f, 4),EnemyEntry(3f,8f,5),EnemyEntry(3f,9f, 6),EnemyEntry(4f,10f, 7)
    )::: List.fill(9)(new EnemyEntry(Random.nextInt(MapDefaults.SIZE_X),Random.nextInt(MapDefaults.SIZE_Y),Random.nextInt(8),true));

  //val tx139Drw =  robot139.fold(right => null, left => left.angleTextureData(0).standPhases(0))
  val draw139: (Float,Float, Int) => Unit = if (robot139.isLeft) ((x,y, angle) => simpleDrawOnScreen(x,y)) else ((x,y, angle)=> {
    val r = robot139.right.get.angleTextureData(angle).standPhases(0);
    r.texture.draw( x - r.spec.origWidth/2, y-r.spec.origHeight );
  }) ;

    private val rect = Rect((0,0),(10,10));

  var locA = 0;
    def getDrawable : Drawable = new Drawable {
      def draw() {
        val mx = GlobalDebugState.cursorMovementX;
        val my = GlobalDebugState.cursorMovementY;

        glPushMatrix();
        glTranslatef(-mx,-my,0);

        for (enemy <- allEnemies) {
          import enemy._;


          val (curX, curY) = {
            if (isOdd(mapPosY.toInt))
              (DEF_WIDTH * mapPosX + HALF_DEF_WIDTH, DEF_HEIGHT / 2 * (mapPosY - 1) + HALF_DEF_HEIGHT)
            else
              ((DEF_WIDTH * mapPosX) - (DEF_WIDTH / 2) + HALF_DEF_WIDTH, (DEF_HEIGHT / 2 * mapPosY) - (DEF_HEIGHT / 2) + HALF_DEF_HEIGHT);
          }

          if (curX-mx >= -200 && curX-mx <= 1024 && curY-my >= - 200 && curY-my <= 768){
            if (rotateAnim) {
              draw139(curX,curY,locA/100);
            } else {
              draw139(curX,curY,a);
            }

            locA +=1;
            if (locA >= 800) locA =0;

          }
        }


        glPopMatrix();
      }
    }

     def simpleDrawOnScreen(x: Float, y: Float){
       glDisable(GL_TEXTURE_2D);
       glColor3f(0f,0f,0f);
       glPushMatrix();
       glTranslatef(x,y,0);
       glBegin(GL_QUADS);
         rect.directDrw();
       glEnd();
       glPopMatrix();
       glEnable(GL_TEXTURE_2D);
     }
}
