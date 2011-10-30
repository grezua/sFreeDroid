package org.grez.sfreedroid.textures

import org.lwjgl.opengl.GL11._
import java.util.logging.{Level, Logger}

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:35 PM
 * Texture store + draw
 */

private[textures] object FreeTxId {
  import collection.mutable.HashSet

  val usedIds: HashSet[Int] = new HashSet[Int]();
  var fc: Int = 1;

  def findFreId():Int = {
     while(usedIds.contains(fc)){
        fc+=1;
     };
    fc;
  }

  def put(id: Int){
    Logger.getLogger("Texture").log(Level.CONFIG, "txid="+id)
    usedIds+=id;
  }
}



class Texture(imgData: ImgData, txID:Int, val widt: Int, val heg: Int){

    val srcPixelFormat = if (imgData.alpha) GL_RGBA else GL_RGB;
    glBindTexture(GL_TEXTURE_2D, txID);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imgData.w, imgData.h, 0, srcPixelFormat, GL_UNSIGNED_BYTE, imgData.buf);
    FreeTxId.put(txID);

  def this(imgData: ImgData, widt: Int, heg: Int) = this(imgData,FreeTxId.findFreId(),widt,heg);

  def this(imgData: ImgData) = this(imgData,FreeTxId.findFreId(),imgData.w,imgData.h);

    def draw()  {
      glBindTexture(GL_TEXTURE_2D, txID);
       glBegin(GL_QUADS)
        glTexCoord2f(0.0f, 0.0f)
        glVertex2f(0.0f, 0.0f)

        glTexCoord2f(1.0f, 0.0f)
        glVertex2f(widt, 0.0f)

        glTexCoord2f(1.0f, 1.0f)
        glVertex2f(widt, heg)

        glTexCoord2f(0.0f, 1.0f)
        glVertex2f(0.0f, heg)

      glEnd();
    }

  def draw(x:Float, y: Float) {
       glPushMatrix();
       glTranslatef(x, y, 0);
       draw();
       glPopMatrix();
  }
}