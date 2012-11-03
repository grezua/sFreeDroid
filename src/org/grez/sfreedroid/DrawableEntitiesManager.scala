package org.grez.sfreedroid

import collection.immutable.HashMap
import drawable.{OnMousePosUpdate, TextDrawable, Drawable}


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


  private def updSortedList() {
    sortedList =  entities.values.toList.sortBy(_._1).unzip._2
  }

  def updMousePos(x: Int, y: Int){
    mouseableEntities.values.foreach(_.updateMousePos(x,y));
  }

  def drawAll(){
    sortedList.foreach(_.draw());
  }

  def addEntity(name: String, entity: Drawable, layer: Int = 0){
    entities += ((name,(layer,entity)));
    updSortedList();

    if (entity.isInstanceOf[OnMousePosUpdate]){
      mouseableEntities += ((name,entity.asInstanceOf[OnMousePosUpdate]));
    }
  }

/*  def addMouseableEntity(name: String, entity: Drawable with Mouseable, layer: Int = 0){

    entities += ((name,(layer,entity)));
    updSortedList();
  }*/

  def deleteEntry(name: String) {
    mouseableEntities -= name;
    entities -= name;
    updSortedList();
  }

  def listAllEntries() = {
    entities.keys.toList;
  }

}
