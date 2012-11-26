package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.textures.Texture
import java.nio.ByteBuffer


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/26/12
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */

object NumPhases {
  def apply(byteBuffer: ByteBuffer) = new NumPhases(
    walk = byteBuffer.getShort,
    attack = byteBuffer.getShort,
    get_hit = byteBuffer.getShort,
    death = byteBuffer.getShort,
    stand = byteBuffer.getShort)
};

case class NumPhases(walk: Int, attack: Int, get_hit: Int, death: Int, stand: Int);

object TxSpec {
  def apply(byteBuffer: ByteBuffer) = new TxSpec(
    width = byteBuffer.getShort,
    height = byteBuffer.getShort,
    xoff = byteBuffer.getShort,
    yoff = byteBuffer.getShort,
    origWidth = byteBuffer.getShort,
    origHeight = byteBuffer.getShort)
}

case class TxSpec(width: Int, height: Int, xoff: Int, yoff: Int, origWidth: Int, origHeight: Int) {
  def getDataSize = 4 * width * height;
  def getPitch = 4 * width;
}

case class TextureWithSpec(id: Int, texture: Texture, spec: TxSpec);

case class AngleTextureData(
walkPhases:     Array[TextureWithSpec],
attackPhases:   Array[TextureWithSpec],
get_hitPhases:  Array[TextureWithSpec],
deathPhases:    Array[TextureWithSpec],
standPhases:    Array[TextureWithSpec]);

class RobotDrawable(val name: String, val phases: NumPhases, val angleTextureData: Array[AngleTextureData])  {



}
