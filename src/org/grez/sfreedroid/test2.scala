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
import utils.{FPSMeter, NumberUtils}

object tangTest {
  import MapDefaults._

  val X_to_Y_triCoords: List[Int] = {
    /* ok, Lets assume following: ABC - our triangle
    A is point in right angle (0,0) ;B - x-axis point (wdt,0); C is y-axis point (0,heg)
    Now we need to find out all y coordinates that correspond to each x coordinate (that lie on hypotenuse)
    AC/AB = tang<B;
    Y/AX = tang<B;
    Y = AX*tang<B;
     */
    val tang: Float = HALF_DEF_HEIGHT.toFloat / HALF_DEF_WIDTH.toFloat;
    println("tang ="+ tang);
    (for(x <- HALF_DEF_WIDTH to (0,-1) ) yield (x*tang).round).toList
    //list is reversed, because our coordinate system differs a little.
  }

}

object test2   {
  case class TriangHelper(xt: Int, yt: Int, cxOffset: Int, cyOffset: Int);

  def transformCoords(x: Int, y: Int): TriangHelper = {
     import MapDefaults._

      val xt = if (x >HALF_DEF_WIDTH) (DEF_WIDTH -  x,1) else (x,0);
      val yt = if (y > HALF_DEF_HEIGHT)(DEF_HEIGHT -y,1) else (y,-1);
      //center romb is 0,1 // up left 0,0// up rigth 1,0// bottom left 0,2// bottom rigth 1,2

      TriangHelper(xt._1,yt._1,xt._2,yt._2);
    }

  def findOutLocalSelected(th: TriangHelper):(Int,Int) ={
    import tangTest._

    def inTriangle(x:Int, y:Int):Boolean = X_to_Y_triCoords(x) >= y

//    val th = transformCoords(localX,localY);
    if (inTriangle(th.xt, th.yt)) (th.cxOffset,th.cyOffset) else (0,0);
  }

  def main(args: Array[String]) {
    import MapDefaults._
    import NumberUtils.isOdd

    println (tangTest.X_to_Y_triCoords);

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
	 // glBlendFunc(GL_ONE, GL_ZERO);
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

    //val allTestTex: List[Texture] = (for (i <-0 to MapManager.allTestData.size -1) yield new Texture(MapManager.allTestData(i), textureIDBuffer.get(i))).toList;
    val fps: FPSMeter = new FPSMeter();
    var finished = false;

//    imgLoader.allTestData.foreach( (d: ImgData) => println("w:  "+ d.w + "; h: "+ d.h + " ; offsetX:"+d.offsetX+"; offsetY: "+d.offsetY));


    var printb = true;

    def draw(x: Float,y: Float,id: Int) {
      val mt = MapManager.allTestData(id);
          mt.tx.draw(x,y);
          //mt.tx.draw(x+mt.offsetX*0.5f,y+mt.offsetY*0.5f);
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

      val transformed = transformCoords(localMX,localMY)

      val selected = findOutLocalSelected(transformed);

      val selectedX = flatCordMapX+ selected._1;
      val selectedY = (flatCordMapY*2) +selected._2+1;

      if (Mouse.isButtonDown(0)){

        mapaClck(selectedX, selectedY);
      }
      finished = isKeyDown(KEY_ESCAPE) || Display.isCloseRequested;

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
//      glMatrixMode(GL_MODELVIEW)
      glLoadIdentity();
      glColor4f(0.1f,0.0f,0.3f,1.0f)



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
       // if (printb) println(isOdd(y), "; x: ",curX, "; y: ",curY);

      }
      printb = false;
      MapManager.drawGrid(selectedX,selectedY,flatCordMapX,flatCordMapY);
      fps.drawHistogram(20,50);
      FontManager.drawText(800,30,"mouseX="+mx, "ArialGold".capitalize);
      FontManager.drawText(800,60,"mouseY="+my, "ArialGold");
      FontManager.drawText(800,90,"local: ["+localMX+","+localMY+"]", "ArialGold");
      FontManager.drawText(800,120,"flat: ["+flatCordMapX+","+flatCordMapY+"]", "ArialGold");
      FontManager.drawText(800,150,"selected: ["+selectedX+","+selectedY+"]", "ArialGold");
      FontManager.drawText(700,170,"localSector: ["+transformed.xt+","+transformed.yt+","+transformed.cxOffset+","
        +transformed.cyOffset+","+tangTest.X_to_Y_triCoords(transformed.xt)+ "]", "redfont");
      FontManager.drawText(700,190,"transformed: ["+selected._1+","+selected._2+ "]", "redfont");
      FontManager.drawText(800,220,fps.fps+ " fps", "ArialGold");

      fps.endDraw();
      //glColor4f(0.1f,1.0f,1.0f,1.0f)


    }

    Display.destroy();
  }

}