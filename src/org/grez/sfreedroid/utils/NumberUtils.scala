package org.grez.sfreedroid.utils

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 9/26/11
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */

object NumberUtils {
   def isOdd(x:Int) = {(x % 2) == 1}

   def closest2power(s: Int): Int = {
    var l = 2;
    while (l < s) {
      l *= 2;
    }
    return l;
  }
}