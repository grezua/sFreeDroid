package org.grez.sfreedroid.controls

import org.grez.sfreedroid.DrawableEntitiesManager

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 12/15/12
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */

class ToggleButtonOnPanel[T](val value: T, override val text:String, override val x: Int, override val y: Int,
                             val clckbak: (T)=> Unit) extends TextActionButton(text,x,y,  ()=>clckbak(value) ){

  var isPressedState = false;

  override def draw() {
    if (isDown || isPressedState){
          drawButtonBody(pressedBodyColor);
          drawPressedButtonBorder();
        } else {
          drawButtonBody(bodyColor);
          if(isMouseOn){
            drawButtonBorder(selectedBorderColor);
          }else {
            drawButtonBorder(borderColor);
          }
        }
        if (isMouseOn) {
          drawText(selectedTextFont);
        } else {
          drawText(textFont);
        }
  }


  override def mouseUp() {
    if (isDown && !isPressedState) {
      isPressedState = true;
      action()
    };
    isDown = false;
  }
}

class ButtonsSelectorPanel[T] (val x: Int, val y: Int, val layer: Int, onChangeHandler:(T)=>Unit ) extends ControlsPanel{

  private var allButtons: Map[T,ToggleButtonOnPanel[T]] = Map();
  var curX = x;
  var curButton: Option[ToggleButtonOnPanel[T]] = None;

  private def click(v: T){
    curButton.foreach(_.isPressedState = false);
    curButton = allButtons.get(v);
    onChangeHandler(v);
  }

  def addButton(name:String, value: T, text: String){
     val button= new ToggleButtonOnPanel[T](value,text,curX,y,click);
     allButtons += ((value,button));
     curX += button.rect.width + 3;
     addItem(name,button,layer);
  }

}
