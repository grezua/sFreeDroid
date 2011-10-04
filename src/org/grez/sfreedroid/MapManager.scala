package org.grez.sfreedroid

import console.DefaultConsole
import font.FontManager
import io.Source
import org.lwjgl.util.Point
import org.lwjgl.opengl.GL11._
import textures.{Texture, ImageLoad, ImgData, Rect}
import util.Random
import utils.{NumberUtils, FileUtils}

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */

object MapDefaults {
  val SIZE_X = 8;
  val SIZE_Y = 18;
  val NUM_OF_IDS = 40;
  val DEF_WIDTH = 134;
  val DEF_HEIGHT = 66;

  val HALF_DEF_WIDTH =  DEF_WIDTH / 2;
  val HALF_DEF_HEIGHT =  DEF_HEIGHT / 2;

}

case class MapTile (name: String, tx: Texture,  offsetX: Int = 0, offsetY: Int = 0  ){
  DefaultConsole.log(name + "{ w:"+tx.widt+"; h:"+tx.heg+"; ox:"+offsetX+"; oy:"+offsetY+"}");
}

object MapManager {

  import MapDefaults._;

  private def getOffsets(fn: String): (Int, Int) = {
    val src = Source.fromFile(fn );
    val offsetTxtLines = src.getLines();

    def getStrFromLines(str: String): Int = {
      val xStr = offsetTxtLines.find(_.contains(str));
      if (xStr.isEmpty) 0 else Integer.parseInt(xStr.get.substring(str.length(), xStr.get.length()))
    }

    val offsetX = getStrFromLines("OffsetX=");
    val offsetY = getStrFromLines("OffsetY=");

    src.close();

    (offsetX,offsetY);
  }

  def genTestData(fn: String): MapTile = {
    val pngEnding = ".png";
    val offsetEnding = ".offset"

    val imgData = ImageLoad.loadImgFile(fn+pngEnding);
    val offsets = getOffsets(fn +offsetEnding);

    return MapTile(FileUtils.getFileNameWithoutDirAndExt(fn+pngEnding), new Texture(imgData), offsets._1,offsets._2);
  }


  lazy val allTestData: List[MapTile] = generateNames().map(genTestData(_));

  def generateNames(): List[String] = {
    def addLeadingZeroes(v: String): String = {
      if (v.length() < 4) {
        return addLeadingZeroes("0" + v);
      };
      v
    }



    def genList(base: String, To: Int): List[String] = {
      (for {i <- 1 to To} yield base + addLeadingZeroes(i.toString)).toList;
    }

    val base = "./graphics/flor/iso_sidewalk_"; // "./graphics/flor/iso_carpet_tile_" ;
    val base1 = "./graphics/flor/iso_miscellaneous_floor_"

    genList(base, 24) ::: genList(base1, 23);
  }

  def getMapRect(x: Int, y: Int): Rect = {
    val pt = (zx: Int, zy: Int) => new Point(zx, zy);
    val lx = x * DEF_WIDTH;
    val rx = lx + DEF_WIDTH;
    val ty = y * DEF_HEIGHT;
    val by = ty + DEF_HEIGHT;
    Rect(leftTop = pt(lx, ty), rightTop = pt(rx, ty), rightBottom = pt(rx, by), leftBottom = pt(lx, by));
  }

  def getTileRect(x: Int, y: Int): Rect = {
    import NumberUtils.isOdd;
    val pt = (zx: Int, zy: Int) => new Point(zx, zy);


    val lx = (x * DEF_WIDTH) -  {if (!isOdd(y)) HALF_DEF_WIDTH else 0};
    val mx = (lx + HALF_DEF_WIDTH)
    val rx = (lx + DEF_WIDTH)
    val ty = (y * HALF_DEF_HEIGHT) - HALF_DEF_HEIGHT
    val my = (ty + HALF_DEF_HEIGHT)
    val by = (ty + DEF_HEIGHT)


    Rect(leftTop = pt(lx, my), rightTop = pt(mx, ty), rightBottom = pt(rx, my), leftBottom = pt(mx, by));
  }

  /*def drawSelectedMapCell(selectedX: Int, selectedY: Int){

  }*/

  def drawRect (r: Rect) {
     glVertex2i(r.leftTop.getX, r.leftTop.getY);
    glVertex2i(r.rightTop.getX, r.rightTop.getY);

    glVertex2i(r.rightTop.getX, r.rightTop.getY);
    glVertex2i(r.rightBottom.getX, r.rightBottom.getY);

    glVertex2i(r.rightBottom.getX, r.rightBottom.getY);
    glVertex2i(r.leftBottom.getX, r.leftBottom.getY);

    glVertex2i(r.leftBottom.getX, r.leftBottom.getY);
    glVertex2i(r.leftTop.getX, r.leftTop.getY);
  }

  def drawGrid(selectedX: Int, selectedY: Int, flatX:Int, flatY:Int) {
    glDisable(GL_TEXTURE_2D)
    glColor3f(1.0f, 0f, 1.0f);
    glShadeModel(GL_FLAT);
    glBegin(GL_LINES);
    for (i <- 0 to SIZE_X) {
      val curX = i * DEF_WIDTH;
      glVertex2i(curX, 0);
      glVertex2i(curX, 768);
    }
    for (j <- 0 to SIZE_Y) {
      val curY = j * DEF_HEIGHT;
      glVertex2i(0, curY);
      glVertex2i(1024, curY);
    }

    glColor3f(0.0f, 0.0f, 0.0f);

    val outScreenX = SIZE_X*DEF_WIDTH;

    for (i <- 0 to SIZE_X + 10) {
      val curY =  (i * DEF_HEIGHT + HALF_DEF_HEIGHT) ;
      val curX = (outScreenX)- ( i * DEF_WIDTH + HALF_DEF_WIDTH);
      glVertex2i(curX, 0);
      glVertex2i(outScreenX, curY);
    }

    for (j <- 0 to SIZE_Y) {
      val curY = j * DEF_HEIGHT + HALF_DEF_HEIGHT ;
      val curX = j * DEF_WIDTH + HALF_DEF_WIDTH;
      glVertex2i(0, curY);
      glVertex2i(curX, 0);
    }

    glColor3f(1.0f, 1.0f, 0.0f);
    drawRect(getMapRect(flatX, flatY));
    glColor3f(1f,1f,1f);
    val tileRect = getTileRect(selectedX, selectedY);
    drawRect(tileRect);

    glEnd();
    glFlush();
    glEnable(GL_TEXTURE_2D)

  //  FontManager.drawText(tileRect.leftTop.getX, tileRect.leftBottom.getY+30,tileRect.toString, "redfont");

  }

  lazy val mapa: Array[Array[Int]] = {
    val r: Array[Array[Int]] = Array.ofDim(SIZE_X, SIZE_Y);
    for {k <- 0 until SIZE_X; j <- 0 until SIZE_Y} r(k)(j) = Random.nextInt(NUM_OF_IDS);
    r;
  };
}