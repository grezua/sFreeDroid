package org.grez.sfreedroid.utils

import java.util.zip.InflaterInputStream
import java.io.{IOException, FileNotFoundException, FileInputStream, File}
import java.nio.{ByteOrder, ByteBuffer}
import org.grez.sfreedroid.textures.{Texture, ImgData}
import org.grez.sfreedroid.drawable._
import org.lwjgl.opengl.GL32
import scala.Left
import org.grez.sfreedroid.textures.ImgData
import scala.Left
import org.grez.sfreedroid.drawable.AngleTextureData
import org.grez.sfreedroid.drawable.TextureWithSpec
import org.grez.sfreedroid.textures.ImgData
import java.awt.Robot.RobotDisposer

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/9/11
 * Time: 2:47 PM
 * Tests for original texture zip files pack...
 */

class RobotLoader {
  val ENEMY_ROTATION_MODELS_AVAILABLE = 40;
  val ROTATION_ANGLES_PER_ROTATION_MODEL = 8;
  val HEADER_SIZE = 18;
  val IMG_SPEC_SIZE = 12;

  val readBuf: Array[Byte] = new Array[Byte](4096);

  val workBuffer: ByteBuffer = ByteBuffer.wrap(readBuf);
  workBuffer.order(ByteOrder.LITTLE_ENDIAN);

  private def readDataToDirectBuffer(zf: InflaterInputStream, length: Int): ByteBuffer = {
    val resultBuf = ByteBuffer.allocateDirect(length)

    var leftToRead = length;

    while (leftToRead > 0) {
      val nr = if (leftToRead > readBuf.length) readBuf.length else leftToRead;

      val br = zf.read(readBuf, 0, nr);
      //println(br,leftToRead);
      if (br == -1) {
        leftToRead = 0;
      } else {
        resultBuf.put(readBuf, 0, br);
        leftToRead = leftToRead - br;
      }
    }
    resultBuf.flip();
    return resultBuf;
  }

  private def readStream(zf: InflaterInputStream, length: Int) {
    var leftToRead = length;
    var pos = 0;
    while (leftToRead > 0) {
      val br = zf.read(readBuf,pos,leftToRead);
       pos +=br;
       leftToRead -= br;
     }
  }

  //reusing read buf, hope double pitch doesn't exceed 4kb...
  private def flipImageDataVertically(buf: ByteBuffer, height: Int, pitch: Int) {
    val firstMemoryLocation = 0;
    val secondMemoryLocation = pitch + 2;

    var endIdx = height/2;// beginIdx+1;
    var beginIdx = endIdx-1;

    while (beginIdx >=0) {
     buf.position(beginIdx*pitch);
     buf.get(readBuf,firstMemoryLocation,pitch);
     buf.position(endIdx*pitch);
     buf.get(readBuf,secondMemoryLocation,pitch);
     buf.position(beginIdx*pitch);
     buf.put(readBuf,secondMemoryLocation,pitch);
     buf.position(endIdx*pitch);
     buf.put(readBuf,firstMemoryLocation,pitch);

      beginIdx -= 1;
      endIdx += 1;
    }

    buf.position(0);
   // buf.flip();
  }


  private def readNextTx(zf: InflaterInputStream, phaseName: String, angle: Int): TextureWithSpec = {
//  val rb =  zf.read(readBuf,0, IMG_SPEC_SIZE);
    readStream(zf,IMG_SPEC_SIZE);
    workBuffer.position(0);
    val spec = TxSpec(workBuffer);
    println( "read Next("+angle+") -> "+phaseName + " ;spec ="+spec)
    val pixelData = readDataToDirectBuffer(zf, spec.getDataSize)
    flipImageDataVertically(pixelData, spec.height, spec.getPitch);

    val image = new ImgData(pixelData, spec.height, spec.width, true);
    val texture: Texture = new Texture(image,GL32.GL_BGRA)

    new TextureWithSpec(texture.txID, texture, spec)
  }

  //mutable!
  def loadRobot(fileName: String): Either[Exception,RobotDrawable] = {
    import org.grez.sfreedroid.console.{DefaultConsole => console}

    val f: File = new File(fileName);
     if (!f.exists()) {
       console.log("File "+fileName+ "doesn't exists");
       return Left(new FileNotFoundException("File "+fileName+ "doesn't exists"));
     }

    console.log("Start loading "+fileName+" ...");

    val zf: InflaterInputStream = new InflaterInputStream(new FileInputStream(f));
          try {

            readStream(zf,HEADER_SIZE);

            val strType = new String(readBuf, 0, 4);
            val oglType = new String(readBuf, 4, 4);

            console.log(strType);
            console.log(oglType);

            if (strType != "eneX" || oglType != "oglX") return Left(new UnsupportedOperationException("unsupported file format!"))

            workBuffer.position(8);
            val phases = NumPhases(workBuffer);

            console.log("phases "+ phases);


            val angleTextureData: Array[AngleTextureData] =
             for {i <- Array.range(0, ROTATION_ANGLES_PER_ROTATION_MODEL)} yield
              new AngleTextureData(
              walkPhases = Array.fill(phases.walk)(readNextTx(zf,"walk",i)),
              attackPhases = Array.fill(phases.attack)(readNextTx(zf,"attack",i)),
              get_hitPhases = Array.fill(phases.get_hit)(readNextTx(zf,"getHit",i)),
              deathPhases = Array.fill(phases.death)(readNextTx(zf,"death",i)),
              standPhases = Array.fill(phases.stand)(readNextTx(zf,"stand",i))
              );



            return  Right(new RobotDrawable(fileName,phases,angleTextureData));
            /*
           Walk phases: 5 (0 -> 4)
           Attack phases: 5 (5 -> 9)
           Gethit phases: 5 (10 -> 14)
           Death phases: 5 (15 -> 19)
           Stand phases: 8 (20 -> 27)
            */

          }catch {
            case e: IOException => return Left(e);
            case e: Exception => {
              console.log("unexpected Exception "+ e);
              e.printStackTrace();
              return Left(e);
            }

          }finally {
            zf.close();
          }
  }


}