package org.grez.sfreedroid

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: Sep 24, 2010
 * Time: 1:01:13 PM
 * To change this template use File | Settings | File Templates.
 */


import controls.BottomControlsPanelManager
import debug.GlobalDebugState
import org.lwjgl._
import input.{Keyboard, Mouse}
import opengl.{GL32, Display, GL11, DisplayMode}
import GL11._
import java.nio.IntBuffer
import textures.{TextureDrawable, Texture}
import utils.RobotLoader


object test2   {
  import GlobalDebugState.fpsMeter ;
  import GlobalDebugState.{mapDrawable => map};
  import console.{DefaultConsole => console};

  def main(args: Array[String]) {
    init();

    DrawableEntitiesManager.addEntity("map", map,0);
    DrawableEntitiesManager.addEntity("mousepos", map.getMousePosDrawable,2);
    val pannel = new  BottomControlsPanelManager
    pannel.addControlsPanel();

    val l = System.currentTimeMillis();
    val robotLoader = new RobotLoader();
    val robot1 = robotLoader.loadRobot("./graphics/droids/139/139.tux_image_archive.z");

    robot1.fold(left => console.log("robot loading err"), right =>{
      DrawableEntitiesManager.addEntity("r1", new TextureDrawable(right.angleTextureData(1).deathPhases(0).texture,10,10),4);
      DrawableEntitiesManager.addEntity("r2", new TextureDrawable(right.angleTextureData(1).deathPhases(1).texture,90,10),4);
      DrawableEntitiesManager.addEntity("r3", new TextureDrawable(right.angleTextureData(1).deathPhases(2).texture,170,10),4);
      DrawableEntitiesManager.addEntity("r4", new TextureDrawable(right.angleTextureData(1).deathPhases(3).texture,250,10),4);
      DrawableEntitiesManager.addEntity("r5", new TextureDrawable(right.angleTextureData(1).deathPhases(4).texture,330,10),4);
    });

/*    val tx1 = new Texture(RobotLoader.getTestImg("./graphics/droids/139/139.tux_image_archive.z"),GL32.GL_BGRA);
    DrawableEntitiesManager.addEntity("robot1", new TextureDrawable(tx1,10,10),4);

    val tx2 = new Texture(RobotLoader.getTestImg("./graphics/droids/123/123.tux_image_archive.z"),GL32.GL_BGRA);
    DrawableEntitiesManager.addEntity("robot2", new TextureDrawable(tx2,100,250),4);

    val tx3 = new Texture(RobotLoader.getTestImg("./graphics/droids/476/476.tux_image_archive.z"),GL32.GL_BGRA);
    DrawableEntitiesManager.addEntity("robot3", new TextureDrawable(tx3,300,170),4);*/

    console.log("tx load time="+(System.currentTimeMillis() - l));


    var finished = false;

    while (!finished){
      Display.sync(60);
      Display.update();
      processMouse();
      KeyboardHelper.processKeyboard()

      finished = Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Display.isCloseRequested;

      glLoadIdentity();
      glColor4f(0.1f,0.0f,0.3f,1.0f)
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

      DrawableEntitiesManager.drawAll();

      if (console.showing){
         console.draw();
      }

      fpsMeter.endDraw();
    }

    Display.destroy();
  }


  private def processMouse(){
    import Mouse._;

    poll();

    DrawableEntitiesManager.updMousePos(getX, getY);

    if (isButtonDown(0)){
      DrawableEntitiesManager.processMouseDown();
    }else {
      DrawableEntitiesManager.noMouseDown();
    }
  }

  private def init() {

    Display.setTitle("SFreeDroid")
    Display.setFullscreen(false)
    Display.setVSyncEnabled(false)
    Display.setDisplayMode(new DisplayMode(1024, 768))
    Display.create();

    Mouse.create();
    Keyboard.create();
    Keyboard.enableRepeatEvents(true);

    val isvsize = glGetInteger(GL_MAX_TEXTURE_SIZE);
    console.log("GL_MAX_TEXTURE_SIZE = "+ isvsize);

    glEnable(GL_TEXTURE_2D)
    glDisable(GL_DEPTH_TEST)
    glEnable(GL_ALPHA_TEST);
    glAlphaFunc(GL_GREATER, 0.4999f);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
    glEnable(GL_LINE_SMOOTH);
    //    glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

    glViewport(0, 0, 1024, 768)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, 1024, 768, 0, -1, 1)
    glMatrixMode(GL_MODELVIEW)
    //glLoadIdentity

    glClearColor(0.5f, 1.0f, 0.5f, 1.0f);

    //texture ID bind to gl
    val textureIDBuffer: IntBuffer = BufferUtils.createIntBuffer(MapManager.allTestData.size);
    glGenTextures(textureIDBuffer);
  }

  object KeyboardHelper {

    def moveCursorLeft() {
      if (GlobalDebugState.cursorMovementX == 0) return
      GlobalDebugState.cursorMovementX -= 10;
          }

    def moveCursorRight() {
      GlobalDebugState.cursorMovementX += 10;
          }

    def moveCursorUp() {
      if (GlobalDebugState.cursorMovementY == 0) return
      GlobalDebugState.cursorMovementY -= 8;
    }

    def moveCursorDown() {
      GlobalDebugState.cursorMovementY += 8;
    }


    private def typycalKbdProcess(action: () => Unit) {
       import input.Keyboard._
       //if (getEventKeyState) {
        // enableRepeatEvents(true);
         action();
      /// } else {
         //enableRepeatEvents(console.showing);
      // }
     }


    def processKeyboard() {
      import Keyboard._

      poll();

      if (!console.showing) {
        if (isKeyDown(KEY_LEFT)) {
          moveCursorLeft();
        }

        if (isKeyDown(KEY_RIGHT)) {
          moveCursorRight();
        }
        if (isKeyDown(KEY_UP)) {
          moveCursorUp();
        }
        if (isKeyDown(KEY_DOWN)) {
          moveCursorDown();
        }
      }

       while (next()) {

         if (getEventCharacter == '~' && getEventKeyState) {
           console.showing = !console.showing;
          // enableRepeatEvents(console.showing);
         }
         if (console.showing && getEventKeyState) {
           console.addCh(getEventKey, getEventCharacter);
         }



/*         getEventKey match {
           case KEY_LEFT => typycalKbdProcess(moveCursorLeft);
           case KEY_RIGHT => typycalKbdProcess(moveCursorRight);
           case KEY_UP => typycalKbdProcess(moveCursorUp);
           case KEY_DOWN => typycalKbdProcess(moveCursorDown);
           case _ => ;
         }*/

       }
     }
  }
}