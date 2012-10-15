package org.grez.sfreedroid.console

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.font.FontManager
import org.lwjgl.input.Keyboard
import collection.immutable.Queue
import org.grez.sfreedroid.debug.GlobalDebugState

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/4/11
 * Time: 5:58 PM
 * console
 */

class Console(val height:Int, val histSize:Int, val regCmds: List[ConsoleCmd])  {

  val FONT_HEG = 25;
  val LINE_H = (FONT_HEG+4);
  val textLinesCount: Int = (height / LINE_H)-1;
  var showing = false
  var cmd: String = "";
  var historyText: Queue[String] = Queue("");
  var histPosition = 0;
  var historyCmdT: Vector[String] = Vector(""); //todo: add read from file, or something ^_^!
  var histCmdPos = 0;

  private def histUp(){
    if (histPosition >= historyText.size - textLinesCount) return;
    histPosition += 1;
  }

  private def histDown(){
    if (histPosition <= 0) return;
    histPosition -= 1;
  }

 /* private def histCmdUp(): Option[String]{     //todo it later

  }*/

  private def drawRectangle(){
    glShadeModel(GL_FLAT);
    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glColor4f(0.3f, 0f, 0.8f,0.8f);
    glBegin(GL_QUADS);
    glVertex2i(0,0);
    glVertex2i(1024,0);
    glVertex2i(1024,height);
    glVertex2i(0,height);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    glDisable(GL_BLEND);
  }

  def log (value : Any){
    println(value);

    def putTx(txt: String){
      historyText += txt;
      if (historyText.size > histSize) historyText = historyText.dequeue._2;
      }

    var txt = value.toString.replace("\t","    ");

    while (!txt.isEmpty){
        val newLineIndex = txt.indexOf('\n');
        if (newLineIndex > 0 && newLineIndex < 80) {
          val kk = txt.splitAt(newLineIndex);
          putTx(kk._1);
          txt = kk._2.tail;
        } else {
          val kk = txt.splitAt(80);
          txt = kk._2;
          putTx(if (kk._1.length() >79) kk._1+'/' else kk._1 );
        }
      }
  }

  private def drawCmd() {
     FontManager.drawText(2,height-FONT_HEG,cmd+'_', "redfont" );
  }


  def executeCMD(cmd: ConsoleCmd, line: String) {
    val paramsStrings = line.split(" ").toList.filter(_ != "").tail; //first str is cmd itself!
    def convertStringtoAny(s: String): Any = {
      try {
        return s.toInt;
      } catch {
        case _ =>; //is that Ok ??
      }

      try {
        return s.toFloat;
      } catch {
        case _ =>; //is that Ok ??
      }

      try {
        return s.toBoolean;
      } catch {
        case _ =>; //is that Ok ??
      }

      s;
    }

    if (cmd.params.isEmpty){
      cmd.execute(None,this);
      return;
    }

    val params: List[Any] = paramsStrings.map(is => convertStringtoAny(is.trim));

    if ((params.size != cmd.params.get.size) || (None !=
      params.zip(cmd.params.get).find(pair => !CmdParamType.confirmsTo(pair._1, pair._2.paramType)))){
       log("bad parameters!")
    } else {
       cmd.execute(Option(params),this);
     }
  }

  def putCMdToHistory(line: String) {
    historyCmdT = line +: historyCmdT;
          if (historyCmdT.size > histSize) historyCmdT = historyCmdT.take(histSize);
    histCmdPos = -1;
  }

  def searchCmd(s: String): Option[ConsoleCmd] = {
    regCmds.find(cmd => s.startsWith(cmd.cmd));
  }

  def execute(cmd: String) {
    putCMdToHistory(cmd); //history all entered!
    val searchS = searchCmd(cmd);
    if (searchS.isEmpty) {
      log("UNKNOWN CMD");
    } else {
      executeCMD(searchS.get, cmd);
    }
  }

  private def logAllCmds(){
     log(regCmds.foldLeft("")((s,cmd) => s + cmd.cmd + "; ")); //log all commands names into one line
  }



  def addCh(key: Int, c: Char) {
    import org.lwjgl.input.Keyboard._
    cmd = key match {
      case KEY_BACK  => cmd.take(cmd.size - 1) ;
      case KEY_SPACE => cmd + ' ' ;
      case KEY_GRAVE => cmd ;
      case KEY_PRIOR => {
        histUp();
        cmd;
      };
      case KEY_NEXT => {
        histDown();
        cmd;
      };
      case KEY_RETURN => {
        log(cmd);
        execute(cmd.trim.toLowerCase);
        ""
      };
      case _ => c match {
        case '?' => {
         val searchS = searchCmd(cmd);
          if (searchS.isEmpty){
           logAllCmds();
          } else {
            log(searchS.get.fullHelp);
          }
          cmd;
        };
        case pc if FontManager.printableChar(pc,"redfont") => cmd+c;
        case _ => cmd;
      };
    }
  }


  def drawHistText(){

    def recur(txt: Queue[String], yidx: Int){
      if (txt.isEmpty || yidx < -LINE_H) return;

      val elem = txt.dequeue;
      FontManager.drawText(2,yidx,elem._1, "redfont" );
      recur(elem._2, yidx - FONT_HEG)
    }

    recur(historyText.reverse.drop(histPosition), textLinesCount * LINE_H);

  }

  def draw(){
      drawRectangle();
      drawHistText();
      drawCmd();
  }
}


/*Console params and CMDs definition */
object CmdParamType extends Enumeration("CPTInt", "CPTString", "CPTFloat", "CPTBoolean") {
  type CmdParamType = Value;
  val  CPTInt, CPTString, CPTFloat, CPTBoolean = Value;
  def confirmsTo(any: Any, pt: CmdParamType): Boolean = {
    if (any.isInstanceOf[Boolean] && CPTBoolean == pt ) return true;
    if (any.isInstanceOf[Float] && CPTFloat == pt ) return true;
    if (any.isInstanceOf[Int] && CPTInt == pt ) return true;
    if (any.isInstanceOf[String] && CPTString == pt ) return true;

    false;
  }
}



import CmdParamType._; //wierd scala enums

case class CmdParam(name: String, paramType: CmdParamType, help: String);

abstract class ConsoleCmd (val cmd: String, val params: Option[List[CmdParam]]) {
     def fullHelp:String = {
       val paramsHelp = if(params.isEmpty) {
         ""
     } else {
         params.get.foldLeft("\n")((s, param) => s+ "\t" + param.name + ": " + param.help + "\n")
       }
       cmd + ": " + getHelp +  paramsHelp;
     }
     def getHelp: String;
     def execute(params: Option[List[Any]], console: Console);
}


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
    val histLine =  console.historyCmdT.foldLeft("History of entered CMDS: \n")((s,cmd) => s+ "\t"+cmd+"\n" )+ "--END OF HIST--"
    console.log(histLine);
  }
}


/*Default console impl*/
object DefaultConsole extends Console(200,1000, List(GreetCMD,GridSwitchCMD,FewParamsTestCMD,PrintCMDHistoryCMD));