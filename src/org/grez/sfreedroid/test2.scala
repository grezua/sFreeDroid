package org.grez.sfreedroid

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: Sep 24, 2010
 * Time: 1:01:13 PM
 * To change this template use File | Settings | File Templates.
 */


import font.FontManager
import org.lwjgl._
import input.Keyboard._
import input.Mouse
import opengl.{GL14, Display, GL11, DisplayMode}
import GL11._
import java.nio.IntBuffer
import textures.Texture

object test2   {

  def main(args: Array[String]) {
    import MapDefaults._

    Display.setTitle("SFreeDroid")
		Display.setFullscreen(false)
		Display.setVSyncEnabled(false)
		Display.setDisplayMode(new DisplayMode(1024,768))
		Display.create()

    val isvsize = glGetInteger(GL_MAX_TEXTURE_SIZE);
    println (isvsize);

    glEnable(GL_TEXTURE_2D)
    glDisable(GL_DEPTH_TEST)
//    glShadeModel(GL_FLAT);
	  glEnable(GL_ALPHA_TEST);
	  glAlphaFunc(GL_GREATER, 0.4999f);
//	  glDisable(GL_BLEND);
//	  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	  glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
//    glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

    glViewport(0, 0, 1024, 768)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, 1024, 768, 0, -1, 1)
    glMatrixMode(GL_MODELVIEW)
    //glLoadIdentity

    glClearColor(0.5f,1.0f,0.5f,1.0f);

    //texture ID bind to gl
    val textureIDBuffer:IntBuffer = BufferUtils.createIntBuffer(MapManager.allTestData.size);
    glGenTextures(textureIDBuffer);

    val allTestTex: List[Texture] = (for (i <-0 to MapManager.allTestData.size -1) yield new Texture(MapManager.allTestData(i), textureIDBuffer.get(i))).toList;

    var finished = false;

//    imgLoader.allTestData.foreach( (d: ImgData) => println("w:  "+ d.w + "; h: "+ d.h + " ; offsetX:"+d.offsetX+"; offsetY: "+d.offsetY));
//    print( mapa);

    var printb = true;

    def draw(x: Float,y: Float,id: Int) {
          allTestTex(id).draw(x,y);
    }

    def mapaClck(x:Int, y:Int){
      if (MapManager.mapa(x)(y) >= NUM_OF_IDS) MapManager.mapa(x)(y) = 0 else MapManager.mapa(x)(y) +=1;
    }

    while (!finished){
      Display.update()
      Mouse.poll();
      val my = 767-Mouse.getY;
      val mx = Mouse.getX;

      val flatCordMapX = mx / DEF_WIDTH;
      val flatCordMapY = my / DEF_HEIGHT;

      val localMX = mx % DEF_WIDTH;
      val localMY = my % DEF_HEIGHT;

      val selectedX = flatCordMapX;
      val selectedY = flatCordMapY*2+1;

      if (Mouse.isButtonDown(0)){

        mapaClck(selectedX, selectedY);
      }
      finished = isKeyDown(KEY_ESCAPE) || Display.isCloseRequested;

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
//      glMatrixMode(GL_MODELVIEW)
      glLoadIdentity();
      glColor4f(0.1f,0.0f,0.3f,1.0f)



      def isOdd(x:Int) = {(x % 2) == 1}

      var curY = 0;
      var curX = 0;
      // imgLoader.allTestData(0).h / 2 ;


      for {y <-0 until SIZE_Y; x <-0 until SIZE_X  }{

      if (isOdd(y)){
          curX = DEF_WIDTH * x;
          curY = DEF_HEIGHT /2 * (y - 1);
      } else{
        curX = (DEF_WIDTH * x) - (DEF_WIDTH / 2);
        curY = (DEF_HEIGHT /2 * y) - (DEF_HEIGHT / 2);
      }
        draw(curX,curY,MapManager.mapa(x)(y))
        if (printb) println(isOdd(y), "; x: ",curX, "; y: ",curY);

      }
      printb = false;
      MapManager.drawGrid(flatCordMapX,flatCordMapY);
      FontManager.drawText(800,30,"mouseX="+mx, "ArialGold");
      FontManager.drawText(800,60,"mouseY="+my, "ArialGold");
      FontManager.drawText(800,90,"local: ["+localMX+","+localMY+"]", "ArialGold");
      FontManager.drawText(800,120,"flat: ["+flatCordMapX+","+flatCordMapY+"]", "ArialGold");
      FontManager.drawText(800,150,"selected: ["+selectedX+","+selectedY+"]", "ArialGold");

      //glColor4f(0.1f,1.0f,1.0f,1.0f)


    }

    Display.destroy();
  }

}