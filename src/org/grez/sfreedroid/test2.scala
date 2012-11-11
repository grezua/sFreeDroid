package org.grez.sfreedroid

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: Sep 24, 2010
 * Time: 1:01:13 PM
 * To change this template use File | Settings | File Templates.
 */


import console.Console
import controls.BottomControlsPanelManager
import debug.GlobalDebugState

import org.lwjgl._
import input.Keyboard._
import input.{Keyboard, Mouse}
import opengl.{Display, GL11, DisplayMode}
import GL11._
import java.nio.IntBuffer



object test2   {


  def main(args: Array[String]) {
    import GlobalDebugState.fpsMeter ;
    import GlobalDebugState.{mapDrawable => map};
    import console.{DefaultConsole => console};

    init(console);

    DrawableEntitiesManager.addEntity("map", map,0);
    DrawableEntitiesManager.addEntity("mousepos", map.getMousePosDrawable,2);
    val pannel = new  BottomControlsPanelManager
    pannel.addControlsPanel();


    var finished = false;

    while (!finished){
      Display.sync(60);
      Display.update();
      Mouse.poll();
      Keyboard.poll();
      DrawableEntitiesManager.updMousePos(Mouse.getX, Mouse.getY);

      while (Keyboard.next()) {
      //  println(Keyboard.getEventCharacter.toString +" "+ Keyboard.getEventKeyState);
        if (Keyboard.getEventCharacter == '~' && Keyboard.getEventKeyState) {
          //println("showing "+console.showing);
          console.showing = !console.showing;
          Keyboard.enableRepeatEvents(console.showing);
      }
        if (console.showing && Keyboard.getEventKeyState){
          console.addCh(Keyboard.getEventKey, Keyboard.getEventCharacter);
        }
      }


      if (Mouse.isButtonDown(0)){
        DrawableEntitiesManager.processMouseDown();
      }else {
        DrawableEntitiesManager.noMouseDown();
      }

      finished = isKeyDown(KEY_ESCAPE) || Display.isCloseRequested;

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
//      glMatrixMode(GL_MODELVIEW)
      glLoadIdentity();
      glColor4f(0.1f,0.0f,0.3f,1.0f)

      //fps.drawHistogram(20,50);

      DrawableEntitiesManager.drawAll();

      if (console.showing){
         console.draw();
      }

      fpsMeter.endDraw();
      //glColor4f(0.1f,1.0f,1.0f,1.0f)
    }

    Display.destroy();
  }


  private def init(console: Console) {

    Display.setTitle("SFreeDroid")
    Display.setFullscreen(false)
    Display.setVSyncEnabled(false)
    Display.setDisplayMode(new DisplayMode(1024, 768))
    Display.create();

    Mouse.create();
    Keyboard.create();
    Keyboard.enableRepeatEvents(false);

    val isvsize = glGetInteger(GL_MAX_TEXTURE_SIZE);
    console.log("GL_MAX_TEXTURE_SIZE = "+ isvsize);

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

    glClearColor(0.5f, 1.0f, 0.5f, 1.0f);

    //texture ID bind to gl
    val textureIDBuffer: IntBuffer = BufferUtils.createIntBuffer(MapManager.allTestData.size);
    glGenTextures(textureIDBuffer);
  }
}