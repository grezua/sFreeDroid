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
  private var entities: HashMap[String,Drawable] = initializeDefs();

  private def initializeDefs():HashMap[String,Drawable] = {
    HashMap(("",new TextDrawable("hello",400,300)));
  }

  def drawAll(){
    entities.values.foreach(_.draw());
  }

  def addEntity(name: String, entity: Drawable){
    entities += ((name,entity));
  }

  def deleteEntry(name: String) {
    entities -= name;
  }

  def listAllEntries() = {
    entities.values.toList;
  }

}
