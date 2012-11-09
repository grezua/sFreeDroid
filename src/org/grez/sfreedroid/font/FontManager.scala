package org.grez.sfreedroid.font

import org.lwjgl.opengl.GL11._
import java.nio.ByteBuffer
import org.grez.sfreedroid._
import console.DefaultConsole
import textures._
import utils.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/19/11
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */

case class CharConfig(ch: Char, tx: Texture);

class FontConfig(val fn: String, val map: Option[Map[Char, CharConfig]]);

private[font] object fontLoadUtil {
  /*private val MARK_CONST = -16711681;

  private def isMark(v: Int) = {
    v == MARK_CONST;
  }*/

  def mappingFromImg(imgData: ImgData, str: String): Option[Map[Char, CharConfig]] = {
    val result = scala.collection.mutable.Map[Char, CharConfig]();
    val mark= imgData.buf.getInt(0);

    val sTlist: List[Int] = (for {k <- 0 until imgData.w; if (mark == imgData.buf.getInt)} yield k).toList;
    imgData.buf.clear();

    val zlist: List[(Int, Int)] = (for {k <- 0 until sTlist.size - 1; if (sTlist(k) != sTlist(k + 1) - 1)} yield (sTlist(k) + 1, sTlist(k + 1) - 1)).toList;
    //println ("slist.size:"+ sTlist.size+ ";  zlist.size:"+ zlist.size);

    for (i <- 0 until str.length()) {
      val stx = zlist(i)._1;
      val endx = zlist(i)._2;
      val w = endx - stx
      val chr = str.charAt(i);

      val dataBuffer: ByteBuffer = ByteBuffer.allocateDirect(w * (imgData.h - 1) * 4);

      var curStx = 0;
      var curEndx = 0;
      for (k <- 1 until imgData.h) {
        curStx = (stx + k * imgData.w) * 4;
        curEndx = (endx + k * imgData.w) * 4;
      //  println("siz:" + dataBuffer.capacity(), " stx ", curStx, " endx ", curEndx, " w ", (curEndx) - (curStx))
        imgData.buf.limit(curEndx);
        imgData.buf.position(curStx);
        dataBuffer.put(imgData.buf);
      }
      dataBuffer.clear();

      val imgd = new ImgData(dataBuffer, imgData.h - 1, w, imgData.alpha);
      val txtx = new Texture(imgd);
      result.update(chr, CharConfig(ch = chr, tx = txtx));
    }
    imgData.buf.clear();

    if (result.isEmpty) None else Option(result.toMap);
  }
}

private[font] object defaultFontConfig {
  import fontLoadUtil._


  val str = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  private val fontFiles =  FileUtils.getPngFileList("./graphics/font/");

  lazy val defaultMapping = defaultFontMapping();

  private def defaultFontMapping(): Map[String,FontConfig] = {
    import FileUtils.{getFileNameWithoutDirAndExt => rd};
    import ImageLoad.{loadImgFile => getImg};

    val gtfFC = (fn: String)=> {
      DefaultConsole.log("loading font: " +fn);
      new FontConfig(fn,mappingFromImg(getImg(fn),str));
    }

     fontFiles.map((f:String)=>(rd(f),gtfFC(f))).toMap;
  }
}

object FontManager {

  private lazy val allFonts: Map[String,FontConfig] = defaultFontConfig.defaultMapping;

  def printableChar(c: Char): Boolean = {
    defaultFontConfig.str.contains(c);
  }

  def getAllFontNames: List[String] = allFonts.keys.toList;

  def isFontExists(fontName: String): Boolean = allFonts.contains(fontName);

  def getTextWidth(str: String, fontName: String): Int = {
    val fc = allFonts(fontName).map.orNull;
    if (null == fc) return 0;

    str.foldLeft(0)((int,chr) => int + fc.get(chr).orElse(fc.get('?')).get.tx.widt)
  }

  def drawText(x: Int, y: Int, str: String, fontName: String) {

    glPushMatrix();
    glTranslatef(x, y, 0);
    val fc =  allFonts(fontName).map.orNull;
    if (null == fc) return ;

    str.foreach((f: Char) => {
      f match {
        case ' ' => glTranslatef(10, 0, 0);
        case _ => {
          fc.get(f) match {
            case None => {
               fc.get('?').get.tx.draw();
               glTranslatef(10, 0, 0);
            }
            case c: Some[CharConfig] => {
              c.get.tx.draw();
              glTranslatef(c.get.tx.widt, 0, 0);
            }
          }

        }
      }
    });
    glPopMatrix();
  }


}