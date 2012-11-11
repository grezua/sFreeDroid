package org.grez.sfreedroid.textures

import com.twl.PNGDecoder
import javax.imageio.ImageIO
import java.nio.{ByteOrder, ByteBuffer}
import java.awt.color.ColorSpace
import java.awt.{Transparency, Color => AwtColor, Graphics}
import java.awt.image._
import java.util.Hashtable
import java.io.{FileInputStream, InputStream, File => JFile}


/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/25/11
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */

case class ImgData(buf: ByteBuffer, h: Int, w: Int, alpha: Boolean);

object ImageLoad {

  def loadImgFile(fn: String):ImgData = {

    val is: InputStream = new FileInputStream(fn);
    val decoder: PNGDecoder = new PNGDecoder(is);
    val buf: ByteBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth * decoder.getHeight);
    decoder.decode(buf, decoder.getWidth * 4, PNGDecoder.Format.RGBA);
    buf.flip();
    is.close();

    new ImgData(buf, decoder.getHeight, decoder.getWidth, decoder.hasAlpha);
  }




  def getPixelDataForIMG(f: JFile): ImgData = {
    import org.grez.sfreedroid.utils.NumberUtils.closest2power;

    val bufferedImage = ImageIO.read(f);
    val hw = (closest2power(bufferedImage.getHeight()), closest2power(bufferedImage.getWidth()));
    val imgConvertedParams = convertImgBuf(bufferedImage, hw);
    val texImage = imgConvertedParams._1;
    val g: Graphics = texImage.getGraphics();
    g.setColor(new AwtColor(0f, 0f, 0f, 0f));
    g.fillRect(0, 0, hw._2, hw._1);
    g.drawImage(bufferedImage, 0, 0, null);
    val data: Array[Byte] = (texImage.getRaster.getDataBuffer.asInstanceOf[DataBufferByte]).getData();

    val imageBuffer = ByteBuffer.allocateDirect(data.length);
    imageBuffer.order(ByteOrder.nativeOrder);
    imageBuffer.put(data, 0, data.length);
    imageBuffer.flip();
    return new ImgData(imageBuffer, hw._1, hw._2, imgConvertedParams._2);
  }

  def glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), Array[Int](8, 8, 8, 8),
    true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

  def glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), Array[Int](8, 8, 8, 0),
    false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

  private def convertImgBuf(initial: BufferedImage, hw: (Int, Int)): (BufferedImage, Boolean) = if (initial.getColorModel.hasAlpha) {
    val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, hw._2, hw._1, 4, null)
    (new BufferedImage(glAlphaColorModel, raster, false, new Hashtable()), true);
  }
  else {
    val raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, hw._2, hw._1, 3, null)
    (new BufferedImage(glColorModel, raster, false, new Hashtable()), false);
  }


}