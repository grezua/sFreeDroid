package org.grez.ziptest

import java.util.zip.InflaterInputStream
import java.io.{FileInputStream, File}
import java.nio.{ByteOrder, ByteBuffer}

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/9/11
 * Time: 2:47 PM
 * Tests for original texture zip files pack...
 */

object ZipFDTest {
  val ENEMY_ROTATION_MODELS_AVAILABLE = 40;
  val ROTATION_ANGLES_PER_ROTATION_MODEL = 8;

  case class NumPhases(walk: Int, attack: Int, get_hit: Int, death: Int, stand: Int);

  case class Length(width: Int, height: Int, xoff: Int, yoff: Int, origWidth: Int, origHeight: Int) {
    def getArrSize = 4 * width * height;
  }

  def readLength(bb: ByteBuffer): Length = {
    Length(
      width = bb.getShort,
      height = bb.getShort,
      xoff = bb.getShort,
      yoff = bb.getShort,
      origWidth = bb.getShort,
      origHeight = bb.getShort);
  }

  def readLength(zf: InflaterInputStream): Length = {
    val bb = ByteBuffer.allocate(12);
    zf.read(bb.array());
    bb.order(ByteOrder.LITTLE_ENDIAN);
    return readLength(bb);
  }

  def main(args: Array[String]) {
    getTestImg();
  }

  def readLongArray(zf: InflaterInputStream, length: Int): Array[Byte] = {
    println(length);
    val img1: Array[Byte] = new Array[Byte](length);
            var leftToRead = img1.length;
            var pos = 0;

            while (leftToRead > 0) {
              val br = zf.read(img1, pos, leftToRead);
              if (br == -1) {
                leftToRead = 0;
              } else {
                println(br);
                pos = pos + br;
                leftToRead = leftToRead - br;
              }
            }
    return img1;
  }

  def getTestImg(): Array[Byte] = {
    val f: File = new File("./graphics/droids/139/139.tux_image_archive.z");
       println(f.exists())
       val zf: InflaterInputStream = new InflaterInputStream(new FileInputStream(f));
       try {

         //val entries = zf.getNextEntry
         val b: Array[Byte] = new Array[Byte](30);
         val bb: ByteBuffer = ByteBuffer.wrap(b);
         bb.order(ByteOrder.LITTLE_ENDIAN);
         println(zf.read(b));

         val strType = new String(b, 0, 4);
         val oglType = new String(b, 4, 4);

         println(strType);
         println(oglType);

         bb.position(8);
         val phases = NumPhases(
           walk = bb.getShort,
           attack = bb.getShort,
           get_hit = bb.getShort,
           death = bb.getShort,
           stand = bb.getShort);

         println(phases);

         val length = readLength(bb);

         println(length);

         val img1 = readLongArray(zf, length.getArrSize)


         /*val length2 = readLength(zf);

         println(length2);*/

         return img1;

         /*
        Walk phases: 5 (0 -> 4)
        Attack phases: 5 (5 -> 9)
        Gethit phases: 5 (10 -> 14)
        Death phases: 5 (15 -> 19)
        Stand phases: 8 (20 -> 27)
         */

       } finally {
         zf.close();
       }
  }
}