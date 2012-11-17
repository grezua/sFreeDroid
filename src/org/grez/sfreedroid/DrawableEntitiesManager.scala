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
object DrawableEntitiesManager {

  private var entities: Map[String, (Int, Drawable)] = HashMap();
  private var sortedList: List[Drawable] = List();

  private var mouseableEntities: Map[String, OnMousePosUpdate] = HashMap();

  private var controls: Map[String, Control] = HashMap();
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


    /* if (selectedControl.isDefined){
      selectedControl.get._2.updateMousePos(x,y);

      if (!selectedControl.get._2.isMouseOn){
        selectedControl = sortedControlsList.find(p =>  {p._2.updateMousePos(x,y); p._2.isMouseOn})
      }
    } else {
        selectedControl = controls.find(p =>  {p._2.updateMousePos(x,y); p._2.isMouseOn})
    }*/
  }

  def drawAll() {
    sortedList.foreach(_.draw());
  }

  def addEntity(name: String, entity: Drawable, layer: Int = 0) {
    entities += ((name, (layer, entity)));
    updSortedList();

    entity match {
      case c: Control => controls += ((name, c));
      case m: OnMousePosUpdate => mouseableEntities += ((name, m));
      case _ => ();
    }

  }

  def deleteEntries(xs: String*) {
    xs.foreach(deleteEntry(_));
  }

  def deleteEntry(name: String) {
    controls -= name;
    mouseableEntities -= name;
    entities -= name;
    if (selectedControl.isDefined && selectedControl.get._1 == name) {
      setCurrentControl(None);
    }
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
