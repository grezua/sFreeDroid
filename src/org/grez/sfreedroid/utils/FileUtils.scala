package org.grez.sfreedroid.utils

import java.io.{StringReader, FileReader, FileWriter, File}
import io.Source
import scala.Array
import util.Random
import org.grez.sfreedroid.MapDefaults

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/24/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */

object FileUtils {

  def getPngFileList(dir: String):List[String] = getFileListWithMask(dir,".png")
  def getFileListWithMask(dir: String, mask:String):List[String] = getFileListInDir(dir).filter(_.endsWith(mask));
  def getFileListInDir(dir: String):List[String]=new File(dir).list().toList.map(dir+_);

  def getFileNameWithoutDirAndExt(fn:String):String = fn.substring(fn.lastIndexOf('/')+1,fn.lastIndexOf('.'));

  def saveOffsetsToFile(file: String, nenene: Array[Option[(Int,Int)]]) {

    val output: FileWriter = new FileWriter(file);
    nenene.foreach(o => output.write(o.toString + '\n'))
    output.close();
  }

  def loadOffsetsFromFile(file: String): Array[Option[(Int,Int)]] = {
    val src = Source.fromFile(file );
    val lines = src.getLines().toList;
    val r: Array[Option[(Int,Int)]] = Array.fill(lines.size) {None};

    for {i <- 0 until lines.size }{
      if (!lines(i).startsWith("None")){
        val s1 = lines(i).split(",")
        val xo = s1(0).substring("Some((".size,s1(0).length).toInt;
        val yo = s1(1).substring(0, s1(1).indexOf(")")).toInt;
        r(i) = Some(xo,yo);
      }
    }
    r;
  }

  def saveMapToFile(file:String, map:  Array[Array[Int]]) {
    val output: FileWriter = new FileWriter(file);
    for {y <-0 until map(0).length; x <-0 until map.length  }{
      output.write(x +","+y+","+map(x)(y)+ '\n');
    }
    output.close();
  }

  def loadMapFromFile(file:String): Array[Array[Int]] = {
    val r: Array[Array[Int]] = Array.fill(MapDefaults.SIZE_X, MapDefaults.SIZE_Y) {0};
    val src = Source.fromFile(file );
    val lines = src.getLines().toList;
    lines.foreach(l => {
      val s = l.split(',').toList;
      val(x,y,i) = (s(0).toInt,s(1).toInt, s(2).toInt);
      r(x)(y) = i;
    } )
    r;
  }

}