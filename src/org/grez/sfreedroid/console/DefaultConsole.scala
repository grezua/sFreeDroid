package org.grez.sfreedroid.console

import org.grez.sfreedroid.debug.GlobalDebugState
import CmdParamType._
import org.grez.sfreedroid.font.FontManager
import org.grez.sfreedroid.DrawableEntitiesManager
import scala.Some
;

//wierd scala enums

/* Console command Implementations!  */

object GreetCMD extends ConsoleCmd("greet", None) {
  def getHelp = "Test greet command ^_^!"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand("Greetings from The Console greet commandos!");
  }
}

object ToggleCMD extends ConsoleCmd("toggle", Option(List(CmdParam("value", CPTString, "grid, fps, mousepos")))) {
  val FPS = "fps";
  val GRID = "grid"
  val MOUSEPOS = "mousepos"
  val DBGMOUSEPOS = "dbg mousepos"

  def getHelp = "toggle some global state"

  def execute(params: Option[List[Any]], console: Console) {
    if (params.isEmpty) {
      console.logFromCommand("toggle: unknown parameter");
      return;
    }

    params.get(0) match {
      case s: String if s == GRID => {
        if (DrawableEntitiesManager.isEntityPresent(GRID)) {
          DrawableEntitiesManager.deleteEntry(GRID);
        } else {
          DrawableEntitiesManager.addEntity(GRID, GlobalDebugState.mapDrawable.getGridDrawable, 1);
        }
      }
      case s: String if s == FPS => {
        if (DrawableEntitiesManager.isEntityPresent(FPS)) {
          DrawableEntitiesManager.deleteEntry(FPS);
        } else {
          DrawableEntitiesManager.addEntity(FPS, GlobalDebugState.fpsMeter.getFPSDrawable(800,220),2);
        }
      } case s: String if s == MOUSEPOS => {
          if (DrawableEntitiesManager.isEntityPresent(DBGMOUSEPOS)){ //cycle through 3 states: simple mouse pos, debug mouse pos, and none
            DrawableEntitiesManager.deleteEntry(DBGMOUSEPOS);
          } else if (DrawableEntitiesManager.isEntityPresent(MOUSEPOS)){
            DrawableEntitiesManager.deleteEntry(MOUSEPOS);
            DrawableEntitiesManager.addEntity(DBGMOUSEPOS, GlobalDebugState.mapDrawable.getGridDebugDrawable,2);
          } else {
            DrawableEntitiesManager.addEntity(MOUSEPOS, GlobalDebugState.mapDrawable.getMousePosDrawable,2);
          }
      }
      case _ => {
        console.logFromCommand("toggle: unknown parameter");
      }
    }
  }
}

object FewParamsTestCMD extends ConsoleCmd("test", Option(List(CmdParam("value", CPTBoolean, "value bool param! ^_^!"),
  CmdParam("a2", CPTString, "some String Param!"), CmdParam("a3", CPTInt, "Some another Int Param")))) {

  def getHelp = "awesome help hee and ther \n no text attached!"

  def execute(params: Option[List[Any]], console: Console) {
    console.logFromCommand("alot of text is going here \t \n fld;askf';lkasd'f;ldsafk';sdlafk'lfhgiery0q9t8reytgreoqh\n \n skafhkldahflkdhflk\nsdjjhfkjsdahfj\t\nklddjf;sajf")
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

object SetConsoleLogFontCMD extends ConsoleCmd("setconsolefont", Option(List(CmdParam("fontVarIdx", CPTInt, "wich font to change (possible values: 0,1,2)"),
  CmdParam("fontName", CPTString, "name of the font")))) {
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

object DeleteDrawable extends ConsoleCmd("rementity", Some(List(CmdParam("name", CmdParamType.CPTString, "name of entity to delete")))) {
  def getHelp = "delete entity from session"

  def execute(params: Option[List[Any]], console: Console) {
    val entName = params.get(0) match {
      case s: String => s;
      case _ => {
        console.logFromCommand("invalid cmd");
        return;
      };
    };

    DrawableEntitiesManager.deleteEntry(entName);
    console.logFromCommand("-" + entName);
  }

}


/*Default console impl*/
object DefaultConsole extends Console(200, 1000, List(GreetCMD, ToggleCMD, FewParamsTestCMD, PrintCMDHistoryCMD,
  QuitCMD, SetConsoleLogFontCMD, ListAllFontsCMD, ListAllDrawables, DeleteDrawable));