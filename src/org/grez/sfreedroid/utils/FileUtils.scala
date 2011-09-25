package org.grez.sfreedroid.utils

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/24/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */

object FileUtils {

  def getPngFileList(dir: String):List[String] = getFileListWithMask(dir,".png")
  def getFileListWithMask(dir: String, mask:String):List[String] = getFileListInDir(dir).filter(_.endsWith(mask));
  def getFileListInDir(dir: String):List[String]=new File(dir).list().toList.map(dir+_);

  def getFileNameWithoutDirAndExt(fn:String):String = fn.substring(fn.lastIndexOf('/')+1,fn.lastIndexOf('.'));

}