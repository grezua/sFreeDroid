package org.grez.sfreedroid.drawable

import org.grez.sfreedroid.controls.OnMousePosUpdate
import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.debug.GlobalDebugState

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 11/28/12
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
class ScreenPosMouseHelperDrawable extends Drawable with OnMousePosUpdate{
  def draw() {
    val (sx,sy) = GlobalDebugState.meScreenPost;

    val (ht,hb) = (sy,768-sy);
    val (wl,wr) = (sx,1024 - sx);
    val (ltx, lty) = if (ht>wl) (sx-ht,0)else (0,sy-wl);
    val (rtx, rty) = if (ht>wr) (sx+ht,0)else (1024,sy-wr);

    val (lbx, lby) = if (hb>wl) (sx-hb,768)else (0,sy+wl);
    val (rbx, rby) = if (hb>wr) (sx+hb,768)else (1024,sy+wr);


    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LINE_SMOOTH);
    glColor3f(0f, 0f, 0f);
    glBegin(GL_LINES);
     glVertex2i(sx,0);
     glVertex2i(sx,768);

     glVertex2i(0,sy);
     glVertex2i(1024,sy);

     glVertex2i(sx,sy);
     glVertex2i(rtx,rty);

    glVertex2i(sx,sy);
    glVertex2i(ltx,lty);

    glVertex2i(sx,sy);
    glVertex2i(rbx,rby);

    glVertex2i(sx,sy);
    glVertex2i(lbx,lby);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_LINE_SMOOTH);


  }
}
