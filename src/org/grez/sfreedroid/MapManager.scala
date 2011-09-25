package org.grez.sfreedroid

import java.io.{FileInputStream, InputStream}
import com.twl.PNGDecoder
import java.nio.ByteBuffer
import io.Source
import textures.ImgData
import org.lwjgl.util.Point
import org.lwjgl.opengl.GL11._
import util.Random
import org.grez.sfreedroid.textures.Rect

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */

object MapDefaults {
  val SIZE_X = 8;
  val SIZE_Y = 16;
  val NUM_OF_IDS = 40;
  val DEF_WIDTH = 134;
  val DEF_HEIGHT = 94;
}

object MapManager {
    import MapDefaults._;

    def genTestData(fn: String): ImgData = {
    val pngEnding = ".png";
    val offsetEnding = ".offset"
    println(fn);

    val is: InputStream = new FileInputStream(fn + pngEnding);
    val decoder: PNGDecoder = new PNGDecoder(is);
    val buf: ByteBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth * decoder.getHeight);
    decoder.decode(buf, decoder.getWidth * 4, PNGDecoder.Format.RGBA);
    buf.flip();
    is.close();


    val src =  Source.fromFile(fn+offsetEnding);
    val offsetTxtLines = src.getLines();

    def getStrFromLines(str:String):Int = {
      val xStr = offsetTxtLines.find(_.contains(str));
      if (xStr.isEmpty) 0 else Integer.parseInt(xStr.get.substring(str.length(),xStr.get.length()))
    }

    val offsetX = getStrFromLines("OffsetX=");
    val offsetY = getStrFromLines("OffsetY=");

//    println("xStr: " + offsetX+ "; yStr="+offsetY);

    src.close();

    return new ImgData(buf, decoder.getHeight, decoder.getWidth, decoder.hasAlpha);
  }


  lazy val allTestData: List[ImgData] = generateNames().map(genTestData(_));

  def generateNames(): List[String] = {

    def addLeadingZeroes(v: String): String = {
      if (v.length() < 4) {
        return addLeadingZeroes("0"+v);
      };
      v
    }



    def genList(base: String, To: Int): List[String] = {
        (for {i <- 1 to  To} yield base+addLeadingZeroes(i.toString)).toList;
    }

    val base =  "./graphics/flor/iso_sidewalk_";  // "./graphics/flor/iso_carpet_tile_" ;
    val base1 = "./graphics/flor/iso_miscellaneous_floor_"

    genList(base, 24):::genList(base1,23);
  }

    def getMapRect(x:Int, y:Int):Rect = {
    val pt = (zx: Int, zy:Int)=>new Point(zx,zy);
    val lx = x*DEF_WIDTH;
    val rx = lx+ DEF_WIDTH;
    val ty= y*DEF_HEIGHT;
    val by = ty+DEF_HEIGHT;
    Rect(leftTop = pt(lx,ty), rightTop = pt(rx,ty), rightBottom = pt(rx,by), leftBottom = pt (lx,by));
  }

  /*def drawSelectedMapCell(selectedX: Int, selectedY: Int){

  }*/

  def drawGrid(selectedX: Int, selectedY: Int) {
    glDisable(GL_TEXTURE_2D)
    glColor3f(1.0f,0f,1.0f);
    glShadeModel(GL_FLAT);
    glBegin(GL_LINES);
    for (i <- 0 to SIZE_X) {
      val curX = i * DEF_WIDTH;
      glVertex2i(curX, 0);
      glVertex2i(curX, 768);
    }
    for (j <- 0 to SIZE_Y) {
      val curY = j * DEF_HEIGHT;
      glVertex2i(0,curY);
      glVertex2i(1024, curY);
    }

    glColor3f(1.0f,1.0f,0.0f);
    val selectedRect = getMapRect(selectedX,selectedY);
    glVertex2i(selectedRect.leftTop.getX, selectedRect.leftTop.getY);
    glVertex2i(selectedRect.rightTop.getX, selectedRect.rightTop.getY);

    glVertex2i(selectedRect.rightTop.getX, selectedRect.rightTop.getY);
    glVertex2i(selectedRect.rightBottom.getX, selectedRect.rightBottom.getY);

    glVertex2i(selectedRect.rightBottom.getX, selectedRect.rightBottom.getY);
    glVertex2i(selectedRect.leftBottom.getX, selectedRect.leftBottom.getY);

    glVertex2i(selectedRect.leftBottom.getX, selectedRect.leftBottom.getY);
    glVertex2i(selectedRect.leftTop.getX, selectedRect.leftTop.getY);

    glEnd();
    glFlush();
    glEnable(GL_TEXTURE_2D)
  }

  lazy val mapa: Array[Array[Int]] = {
    val r: Array[Array[Int]] = Array.ofDim(SIZE_X,SIZE_Y);
    for {k <-0 until  SIZE_X; j <-0 until   SIZE_Y} r(k)(j) = Random.nextInt(NUM_OF_IDS);
    r;
  };
}