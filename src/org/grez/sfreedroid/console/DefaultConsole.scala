package org.grez.sfreedroid.console

import org.grez.sfreedroid.debug.GlobalDebugState
import CmdParamType._; //wierd scala enums

/* Console command Implementations!  */

object GreetCMD extends ConsoleCmd("greet", None){
  def getHelp = "Test greet command ^_^!"

  def execute(params: Option[List[Any]], console: Console) {
    console.log("Greetings from The Console greet commandos!");
  }
}

object GridSwitchCMD extends ConsoleCmd ("grid", Option(List(CmdParam("value",CPTBoolean,"true or false"))) ){
  def getHelp = "enable or disable draving of grid";

  def execute(params: Option[List[Any]], console: Console) {
    if (params.isEmpty) {
      console.log("grid: unknown parameter");
      return;
    }
    val flag: Boolean = params.get(0) match {
      case g: Boolean => g;
      case _ => {
        console.log("grid: unknown parameter");
        return; //execute probably
      };
    }
    console.log("Setting grid to " + flag);
    GlobalDebugState.DrawGridFlag = flag;
  }
}

object FewParamsTestCMD extends ConsoleCmd ("test", Option(List(CmdParam("value", CPTBoolean, "value bool param! ^_^!"),
  CmdParam("a2", CPTString, "some String Param!"), CmdParam("a3",CPTInt, "Some another Int Param")))){

  def getHelp = "awesome help hee and ther \n no text attached!"

  def execute(params: Option[List[Any]], console: Console) {
    console.log("alot of text is going here \t \n fld;askf';lkasd'f;ldsafk';sdlafk'lfhgiery0q9t8reytgreoqh\n \n skafhkldahflkdhflk\nsdjjhfkjsdahfj\t\nklddjf;sajf")
  }
}

object PrintCMDHistoryCMD extends ConsoleCmd ("printhistory", None){
  def getHelp = "prints all history commands"

  def execute(params: Option[List[Any]], console: Console) {
    val histLine =  console.cmdHistory.getHistoryCmd.foldLeft("History of entered CMDS: \n")((s,cmd) => s+ "\t"+cmd+"\n" )+ "--END OF HIST--"
    console.log(histLine);
  }
}

object QuitCMD extends ConsoleCmd ("quit", None){
  def getHelp = "good bye!"

  def execute(params: Option[List[Any]], console: Console) {
    //val histLine =  console.historyCmdT.foldLeft("History of entered CMDS: \n")((s,cmd) => s+ "\t"+cmd+"\n" )+ "--END OF HIST--"
    console.log("biye biye!");
    System.exit(-1); //todo: polite shotdown routine in the future!
  }
}


/*Default console impl*/
object DefaultConsole extends Console(200,1000, List(GreetCMD,GridSwitchCMD,FewParamsTestCMD,PrintCMDHistoryCMD,
QuitCMD));