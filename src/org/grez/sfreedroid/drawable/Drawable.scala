package org.grez.sfreedroid.drawable

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
trait Drawable {
   def draw();
}

trait OnMousePosUpdate {
   protected var mouseX: Int = 0;
   protected var mouseY: Int = 0;

   def updateMousePos(x: Int, y: Int) {
     this.mouseX = x;
     this.mouseY = y;
   }
}
