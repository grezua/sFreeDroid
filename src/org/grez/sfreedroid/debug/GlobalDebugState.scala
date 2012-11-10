package org.grez.sfreedroid.debug

import org.grez.sfreedroid.utils.FPSMeter
import org.grez.sfreedroid.drawable.MapDrawable

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/30/11
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */

object GlobalDebugState {

  lazy val fpsMeter =  new FPSMeter;
  lazy val mapDrawable = new MapDrawable();

}