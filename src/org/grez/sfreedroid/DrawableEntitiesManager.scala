package org.grez.sfreedroid

import collection.immutable.HashMap
import console.DefaultConsole
import controls.{OnMousePosUpdate, Control}
import drawable.{TextDrawable, Drawable}


/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
case class DrawableEntity(name: String, entity: Drawable, layer: Int = 0 );

object DrawableEntitiesManager {

  private var entities: Map[String, (Int, Drawable)] = HashMap();
  private var sortedList: List[Drawable] = List();

  private var mouseableEntities: Map[String, OnMousePosUpdate] = HashMap();

  private var sortedControlsList: List[(String, Control)] = List();

  private var selectedControl: Option[(String, Control)] = None;
  private var controlWasClicked = false;

  private def updSortedList() {
    val sortedPrimaryList = entities.toList.sortBy(_._2._1); //.unzip._2
    sortedList = sortedPrimaryList.unzip._2.unzip._2;

    sortedControlsList = sortedPrimaryList.filter(_._2._2.isInstanceOf[Control]).map(a => (a._1, a._2._2.asInstanceOf[Control])).reverse

/*    DefaultConsole.log("sortedList =" +sortedList);
    DefaultConsole.log("sortedControls =" +sortedControlsList);*/
  }


  def setCurrentControl(c: Option[(String, Control)]) {
    c match {
      case None if selectedControl.isDefined => {
        selectedControl.get._2.isMouseOn = false;
        selectedControl = None;
      }
      case s: Some[(String, Control)] if selectedControl.isEmpty => {
        selectedControl = s;
        selectedControl.get._2.isMouseOn = true;
      }
      case s: Some[(String, Control)] if (s.get._1 != selectedControl.get._1) => {
        selectedControl.get._2.isMouseOn = false;
        selectedControl = s;
        selectedControl.get._2.isMouseOn = true;
      }
      case _ => {}
    }
  }


  def updMousePos(x: Int, y: Int) {
    val chY = 767 - y;

    mouseableEntities.values.foreach(_.updateMousePos(x, chY));
    val changedControl = sortedControlsList.find(_._2.checkMouseOn(x,chY) );
    setCurrentControl(changedControl);

  }

  def drawAll() {
    sortedList.foreach(_.draw());
  }

  def addEntities(xs: DrawableEntity*) {
    xs.foreach(s => _addEntity(s.name,s.entity,s.layer));
    updSortedList();
  }

  def addEntity(drawableEntity: DrawableEntity){
    _addEntity(drawableEntity.name,drawableEntity.entity,drawableEntity.layer);
    updSortedList();
  }

  def addEntity(name: String, entity: Drawable, layer: Int = 0) {
    _addEntity(name,entity,layer)
    updSortedList();
  }

  private def _addEntity(name: String, entity: Drawable, layer: Int) {
    entities += ((name, (layer, entity)));

        entity match {
          case m: OnMousePosUpdate => mouseableEntities += ((name, m));
          case _ => ();
        }
  }

  def deleteEntities(xs: String*) {
    xs.foreach(_deleteEntity(_));
    updSortedList();
  }

  //for sorting operations optimizations.
  private def _deleteEntity(name: String){
    mouseableEntities -= name;
        entities -= name;
        if (selectedControl.isDefined && selectedControl.get._1 == name) {
          setCurrentControl(None);
      }
  }

  def deleteEntity(name: String) {
    _deleteEntity(name);
    updSortedList();
  }

  def listAllEntries() = {
    entities.keys.toList;
  }

  def isEntityPresent(name: String): Boolean = {
    entities.contains(name);
  }

  def processMouseDown() {
    if (selectedControl.isDefined && !controlWasClicked) {
      selectedControl.get._2.mouseDown();
      controlWasClicked = true;
    }
  }

  def noMouseDown() {
    if (controlWasClicked) {
      if (selectedControl.isDefined) {
        selectedControl.get._2.mouseUp();
      }
      controlWasClicked = false;
    }
  }

}
