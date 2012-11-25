package org.grez.ziptest

import java.util.zip.InflaterInputStream
import java.io.{FileInputStream, File}
import java.nio.{ByteOrder, ByteBuffer}
import org.grez.sfreedroid.textures.ImgData

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
    getTestImg("./graphics/droids/139/139.tux_image_archive.z");
  }

  def readDataToDirectBuffer(zf: InflaterInputStream, length: Int): ByteBuffer = {
    println(length);
    val resultBuf = ByteBuffer.allocateDirect(length)
    val readBuf: Array[Byte] = new Array[Byte](4096);
            var leftToRead = length;

            while (leftToRead > 0) {
              val nr = if (leftToRead>readBuf.length)readBuf.length else leftToRead;

              val br = zf.read(readBuf,0,nr);
              println(br,leftToRead);
              if (br == -1) {
                leftToRead = 0;
              } else {
                resultBuf.put(readBuf,0,br);
                leftToRead = leftToRead - br;
              }
            }
    resultBuf.flip();
    return resultBuf;
  }

  def flipImageDataVertically(buf: ByteBuffer, height: Int, pitch: Int) {
    println("pitch= "+pitch)
    val tb: Array[Array[Byte]] = Array.ofDim(2,pitch);

    var endIdx = height/2;// beginIdx+1;
    var beginIdx = endIdx-1;

    while (beginIdx >=0) {
     buf.position(beginIdx*pitch);
     buf.get(tb(0));
     buf.position(endIdx*pitch);
     buf.get(tb(1));
     buf.position(beginIdx*pitch);
     buf.put(tb(1));
     buf.position(endIdx*pitch);
     buf.put(tb(0));

      beginIdx -= 1;
      endIdx += 1;
    }

    buf.flip();
  }

  def getTestImg(fileName: String): ImgData = {
    val f: File = new File(fileName);
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

         val img1 = readDataToDirectBuffer(zf, length.getArrSize)

         flipImageDataVertically(img1,length.height, length.width*4);
         /*val length2 = readLength(zf);

         println(length2);*/

         return new ImgData(img1, length.height, length.width, true);

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