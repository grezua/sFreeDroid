package org.grez.sfreedroid

import collection.immutable.HashMap
import controls.{OnMousePosUpdate, Control}
import drawable.{TextDrawable, Drawable}


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
object DrawableEntitiesManager {


  private var entities: Map[String,(Int,Drawable)] = HashMap();
  private var sortedList: List[Drawable] = List();

  private var mouseableEntities: Map[String, OnMousePosUpdate] = HashMap();
  private var controls: Map[String, Control] = HashMap();

  private var selectedControl: Option[Control] = None;
  private var controlWasClicked = false;

  private def updSortedList() {
    sortedList =  entities.values.toList.sortBy(_._1).unzip._2
  }

  def updMousePos(x: Int, y: Int){
    mouseableEntities.values.foreach(_.updateMousePos(x,y));
    if (selectedControl.isDefined){
      selectedControl.get.updateMousePos(x,y);

      if (!selectedControl.get.isMouseOn){
        selectedControl = controls.values.find(p =>  {p.updateMousePos(x,y); p.isMouseOn})
      }
    } else {
        selectedControl = controls.values.find(p =>  {p.updateMousePos(x,y); p.isMouseOn})
    }
  }

  def drawAll(){
    sortedList.foreach(_.draw());
  }

  def addEntity(name: String, entity: Drawable, layer: Int = 0){
    entities += ((name,(layer,entity)));
    updSortedList();

    entity match {
      case c: Control => controls +=((name, c));
      case m: OnMousePosUpdate => mouseableEntities += ((name,m));
      case _  => ();
    }

  }

  def deleteEntries(xs: String*){
    xs.foreach( deleteEntry(_));
  }

  def deleteEntry(name: String) {
    controls -= name;
    mouseableEntities -= name;
    entities -= name;
    updSortedList();
  }

  def listAllEntries() = {
    entities.keys.toList;
  }

  def isEntityPresent(name: String): Boolean = {
    entities.contains(name);
  }

  def processMouseDown(){
    if (selectedControl.isDefined && !controlWasClicked) {
      selectedControl.get.mouseDown();
      controlWasClicked = true;
    }
  }

  def noMouseDown(){
    if (controlWasClicked) {
       selectedControl.get.mouseUp();
       controlWasClicked = false;
    }
  }

}
