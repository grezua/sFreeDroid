package org.grez.sfreedroid

import collection.immutable.HashMap
import drawable.{TextDrawable, Drawable}

/**
 * Created with IntelliJ IDEA.
 * User: grez
 * Date: 10/27/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
object DrawableEntitiesManager {
  private var entities: HashMap[String,(Int,Drawable)] = HashMap();
  private var sortedList: List[Drawable] = List();


  def updSortedList() {
    sortedList =  entities.values.toList.sortBy(_._1).unzip._2
  }

  def drawAll(){
    sortedList.foreach(_.draw());
  }

  def addEntity(name: String, entity: Drawable, layer: Int = 0){
    entities += ((name,(layer,entity)));
    updSortedList();
  }

  def deleteEntry(name: String) {
    entities -= name;
    updSortedList();
  }

  def listAllEntries() = {
    entities.keys.toList;
  }

}
