package org.grez.sfreedroid.console

import org.grez.sfreedroid.debug.GlobalDebugState
import CmdParamType._
import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.{MapDefaults, MapManager, DrawableEntitiesManager}
import scala.Some
import org.grez.sfreedroid.utils.FileUtils

/* Console command Implementations!  */

object GreetCMD extends ConsoleCmd("greet", None) {
  def getHelp = "Test greet command ^_^!"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand("Greetings from The Console greet commandos!");
  }
}

object ToggleCMD extends ConsoleCmd("toggle", CmdParamsList(CmdParam("value", CPTString, "grid, fps, mousepos", AutoIdentList("grid", "fps", "mousepos")))) {
  val FPS = "fps";
  val FPSHISTOGRAMM = "fps_histogramm";
  val GRID = "grid"
  val MOUSEPOS = "mousepos"
  val DBGMOUSEPOS = "dbg_mousepos"

  def getHelp = "toggle some global state"

  def execute(params: Option[List[Any]], console: Console) {
    if (params.isEmpty) {
      console.logFromCommand("toggle: unknown parameter");
      return;
    }

    params.get(0) match {
      case s: String if s == GRID => {
        if (DrawableEntitiesManager.isEntityPresent(GRID)) {
          DrawableEntitiesManager.deleteEntity(GRID);
        } else {
          DrawableEntitiesManager.addEntity(GRID, GlobalDebugState.mapDrawable.getGridDrawable, 1);
        }
      }
      case s: String if s == FPS => {
        if (DrawableEntitiesManager.isEntityPresent(FPS)) {
          DrawableEntitiesManager.deleteEntity(FPS);
          DrawableEntitiesManager.addEntity(FPSHISTOGRAMM, GlobalDebugState.fpsMeter.getHistogramDrawable(800, 220), 2);
        } else if (DrawableEntitiesManager.isEntityPresent(FPSHISTOGRAMM)) {
          DrawableEntitiesManager.deleteEntity(FPSHISTOGRAMM);
        } else {
          DrawableEntitiesManager.addEntity(FPS, GlobalDebugState.fpsMeter.getFPSDrawable(800, 220), 2);
        }
      }
      case s: String if s == MOUSEPOS => {
        if (DrawableEntitiesManager.isEntityPresent(DBGMOUSEPOS)) {
          //cycle through 3 states: simple mouse pos, debug mouse pos, and none
          DrawableEntitiesManager.deleteEntity(DBGMOUSEPOS);
        } else if (DrawableEntitiesManager.isEntityPresent(MOUSEPOS)) {
          DrawableEntitiesManager.deleteEntity(MOUSEPOS);
          DrawableEntitiesManager.addEntity(DBGMOUSEPOS, GlobalDebugState.mapDrawable.getGridDebugDrawable, 2);
        } else {
          DrawableEntitiesManager.addEntity(MOUSEPOS, GlobalDebugState.mapDrawable.getMousePosDrawable, 2);
        }
      }
      case _ => {
        console.logFromCommand("toggle: unknown parameter");
      }
    }
  }
}

object PrintCMDHistoryCMD extends ConsoleCmd("printhistory", None) {
  def getHelp = "prints all history commands"

  def execute(params: Option[List[Any]], console: Console) {
    val histLine = console.cmdHistory.getHistoryCmd.foldLeft("History of entered CMDS: \n")((s, cmd) => s + "\t" + cmd + "\n") + "--END OF HIST--"
    console.logFromCommand(histLine);
  }
}

object QuitCMD extends ConsoleCmd("quit", None) {
  def getHelp = "good bye!"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand("biye biye!");
    System.exit(-1); //todo: polite shotdown routine in the future!
  }
}

private object FontAutoIdentList extends SecondLevelAutoidentListProvider {
  def getStrList = FontManager.getAllFontNames;
}

object SetConsoleLogFontCMD extends ConsoleCmd("setconsolefont", CmdParamsList(CmdParam("fontVarIdx", CPTInt, "wich font to change (possible values: 0,1,2)", AutoIdentList("0", "1", "2")),
  CmdParam("fontName", CPTString, "name of the font", Some(FontAutoIdentList)))) {
  def getHelp = "sets the font for console"

  def execute(params: Option[List[Any]], console: Console) {
    val fontName = params.get(1) match {
      case s: String => s;
      case _ => {
        console.logFromCommand("invalid cmd");
        return;
      };
    };

    val fontIdx = params.get(0) match {
      case i: Int if (i >= 0 && i <= 2) => i;
      case _ => {
        console.logFromCommand("invalid cmd");
        return;
      }
    }

    if (FontManager.isFontExists(fontName)) {
      console.logFromCommand("Setting console font to '" + fontName + "'");
      console.logFontIndex.update(fontIdx, fontName);
    } else {
      console.logFromCommand("font " + fontName + "doesn't exists! \nYou can list existing fonts by \"listfonts\" cmd")
    }
  }
}

object ListAllFontsCMD extends ConsoleCmd("listfonts", None) {
  def getHelp = "prints all avialable font names"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand(FontManager.getAllFontNames.foldLeft("Font list: \n")((l, s) => l + "  " + s));
  }
}

object ListAllDrawables extends ConsoleCmd("listentities", None) {
  def getHelp = "prints all entities in session"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand(DrawableEntitiesManager.listAllEntries().foldLeft("Entities: \n")((l, s) => l + " " + s));
  }
}

private object EntitiesAutoIdentHelper extends SecondLevelAutoidentListProvider {
  def getStrList = DrawableEntitiesManager.listAllEntries();
}

object DeleteDrawable extends ConsoleCmd("rementity", CmdParamsList(CmdParam("name", CmdParamType.CPTString, "name of entity to delete", Some(EntitiesAutoIdentHelper)))) {
  def getHelp = "delete entity from session"

  def execute(params: Option[List[Any]], console: Console) {
    val entName = params.get(0) match {
      case s: String => s;
      case _ => {
        console.logFromCommand("invalid cmd");
        return;
      };
    };

    DrawableEntitiesManager.deleteEntity(entName);
    console.logFromCommand("-" + entName);
  }
}

object NextMapTile extends ConsoleCmd("nexttile", None) {
  def getHelp = "cycle through flor tiles for selected map pos"

  def execute(params: Option[List[Any]], console: Console) {
    val selected = GlobalDebugState.selectedMapTile
    if (selected.isDefined) {
      val (x, y) = selected.get;
      import MapManager._;
      if (mapa(x)(y) >= MapDefaults.NUM_OF_IDS) mapa(x)(y) = 0 else mapa(x)(y) += 1;
      console.log("set id= " + mapa(x)(y))
    } else {
      console.log("tile is not selected")
    }
  }
}

object PreviousMapTile extends ConsoleCmd("prevtile", None) {
  def getHelp = "cycle backwards through flor tiles for selected map pos"

  def execute(params: Option[List[Any]], console: Console) {
    val selected = GlobalDebugState.selectedMapTile
    if (selected.isDefined) {
      val (x, y) = selected.get;
      import MapManager._;
      if (mapa(x)(y) <= 0) mapa(x)(y) = MapDefaults.NUM_OF_IDS else mapa(x)(y) -= 1;
      console.log("set id= " + mapa(x)(y))
    } else {
      console.log("tile is not selected")
    }
  }
}

object SetMapTile extends ConsoleCmd("settile", CmdParamsList(CmdParam("val", CmdParamType.CPTInt, "tile index to set"))) {
  def getHelp = "cycle backwards through flor tiles for selected map pos"

  def execute(params: Option[List[Any]], console: Console) {
    val id = params.get(0) match {
      case i: Int if i >= 0 && i <= MapDefaults.NUM_OF_IDS => i;
      case i: Int => {
        console.log("invalid tile index, max allowed tile indexes: " + MapDefaults.NUM_OF_IDS);
        return;
      }
      case _ => {
        console.logFromCommand("invalid cmd");
        return;
      }
    }
    val selected = GlobalDebugState.selectedMapTile
    if (selected.isDefined) {
      val (x, y) = selected.get;
      import MapManager._;
      mapa(x)(y) = id;
      console.log("set id= " + mapa(x)(y))
    } else {
      console.log("tile is not selected")
    }
  }
}

object SaveTileOffsets extends ConsoleCmd("saveoffsets", None) {
  def getHelp = "saves edited offsets to a file"

  def execute(params: Option[List[Any]], console: Console) {
    FileUtils.saveOffsetsToFile(MapDefaults.TILE_OFFSETS_FILE, MapManager.tileOffsets);
  }
}

object SaveMap extends ConsoleCmd("savemap", None) {
  def getHelp = "saves map to a file"

  def execute(params: Option[List[Any]], console: Console) {
    FileUtils.saveMapToFile(MapDefaults.MAP_FILE, MapManager.mapa);
  }
}

object SetScreenPos extends ConsoleCmd("setscreenpos", CmdParamsList(CmdParam("x", CmdParamType.CPTInt, "new X pos"), CmdParam("y", CmdParamType.CPTInt, "new Y pos"))) {
  def getHelp = "set screen pos for helper"

  def execute(params: Option[List[Any]], console: Console) {
    val newX = params.get(0) match {
      case i: Int => i;
      case _ => {
        console.log("errr");
        return;
      }
    }

    val newY = params.get(1) match {
      case i: Int => i;
      case _ => {
        console.log("errr");
        return;
      }
    }

    console.log("setting new screen pos (" + newX + "," + newY + ")")
    GlobalDebugState.meScreenPost = (newX, newY);
  }
  }




/*Default console impl*/
object DefaultConsole extends Console(200, 1000, List(GreetCMD, ToggleCMD, PrintCMDHistoryCMD, QuitCMD,
  SetConsoleLogFontCMD, ListAllFontsCMD, ListAllDrawables, DeleteDrawable, NextMapTile, PreviousMapTile,
  SetMapTile, SaveTileOffsets, SaveMap, SetScreenPos));