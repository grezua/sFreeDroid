package org.grez.sfreedroid.controls

import org.grez.sfreedroid.DrawableEntity
import org.grez.sfreedroid.drawable.{AnimDrawableSubstitute, Drawable}

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 12/15/12
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ControlsPanel {
 type DrawLayer = Int;
 protected var items: List[DrawableEntity] = List();

 def addItem(name: String, item: Drawable, drwLayer: DrawLayer){
     items ::= DrawableEntity(name, item, drwLayer);
 }


 def getEntries = items ;

 def getEntriesNames = items.map(_.name);

 def getAnimList: List[AnimDrawableSubstitute] = items.flatMap(_.entity match {
    case d: AnimDrawableSubstitute => Some(d);
    case b: TextActionButton => Some(b.getAnimDrawableSubstitute);
    case _ =>  None;
  });

}
