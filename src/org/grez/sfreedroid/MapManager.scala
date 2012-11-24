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
  val SIZE_X = 25;
  val SIZE_Y =  50;
  val NUM_OF_IDS = 52;
  val DEF_WIDTH = 134;
  val DEF_HEIGHT = 66;

  val HALF_DEF_WIDTH =  DEF_WIDTH / 2;
  val HALF_DEF_HEIGHT =  DEF_HEIGHT / 2;

  val TILE_OFFSETS_FILE =  "./graphics/offset_file.txt";
  val MAP_FILE =  "./graphics/map_file.txt";

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



    def genList(base: String,  From: Int, To: Int): List[String] = {
      (for {i <- From to To} yield base + addLeadingZeroes(i.toString)).toList;
    }

    val base = "./graphics/flor/iso_sidewalk_"; // "./graphics/flor/iso_carpet_tile_" ;
    val base1 = "./graphics/flor/iso_miscellaneous_floor_"
    val base2 = "./graphics/flor/iso_sand_floor_"

    genList(base, 0,24) ::: genList(base1, 0,23) ::: genList(base2, 1,6);
  }


  lazy val mapa: Array[Array[Int]] = FileUtils.loadMapFromFile(MAP_FILE, SIZE_X, SIZE_Y); //Array.fill(SIZE_X, SIZE_Y) {Random.nextInt(NUM_OF_IDS)};

  lazy val tileOffsets: Array[Option[(Int,Int)]] = FileUtils.loadOffsetsFromFile(TILE_OFFSETS_FILE);


  private val cr = Rect((0,0), (SIZE_X - 1,SIZE_Y - 1));

  def coorsWithinMap(x: Int, y: Int): Boolean = {
     cr.isCordsWithin(x,y);
  }
}