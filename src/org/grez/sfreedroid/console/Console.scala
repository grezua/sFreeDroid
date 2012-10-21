package org.grez.sfreedroid.console

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.font.FontManager
import org.lwjgl.input.Keyboard
import collection.immutable.Queue

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: 10/4/11
 * Time: 5:58 PM
 * console
 */

class ConsoleCMDHistory(val histSize: Int){
    private var historyCmdT: Vector[String] = Vector(); //todo: add read from file, or something ^_^!
    private var histCmdPos = 0;
    private var tempCMdForHistory = "";

  def putCMdToHistory(line: String) {
    historyCmdT = line +: historyCmdT;
          if (historyCmdT.size > histSize) historyCmdT = historyCmdT.take(histSize);
    histCmdPos = -1;
  }

  def historyPosDown(localCmd: String): String = {
          if (histCmdPos < 0)
            localCmd;
          else if (histCmdPos == 0) {
            histCmdPos -= 1;
            tempCMdForHistory;
          }
          else {
            histCmdPos -= 1;
            historyCmdT(histCmdPos);
          }
        }

  def historyPosUp(localCmd: String): String = {
    if (histCmdPos >= historyCmdT.size - 1)
      localCmd;
    else {
      if (histCmdPos == -1) tempCMdForHistory = localCmd;

      histCmdPos += 1;
      historyCmdT(histCmdPos);
    }
  }

   def getHistoryCmd = historyCmdT;
}

class ConsoleLogHistory(val histSize: Int, var textLinesCount: Int){
   private var historyText: Queue[String] = Queue("");
   private var histPosition = 0;

  def getHistoryText = historyText;
  def getHistPosition = histPosition;

  def histUp(){
    if (histPosition >= historyText.size - textLinesCount) return;
    histPosition += 1;
  }

  def histDown(){
    if (histPosition <= 0) return;
    histPosition -= 1;
  }

  def putHistory(line: String) {

    def putTx(txt: String){
      historyText += txt;
      if (historyText.size > histSize) historyText = historyText.dequeue._2;
      }

    var txt = line.replace("\t","    ");

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
}



class Console(val height:Int, val histSize:Int, val regCmds: List[ConsoleCmd])  {

  val FONT_HEG = 25;
  val LINE_H = (FONT_HEG+4);
  val textLinesCount: Int = (height / LINE_H)-1;

  var consoleHistFontName = "font05" ;

  var showing = false
  var cmd: String = "";

  val logHistory = new ConsoleLogHistory(histSize, textLinesCount);
  val cmdHistory = new ConsoleCMDHistory(histSize);


  def log (value : Any){
    println(value);
    logHistory.putHistory(value.toString);
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


  def searchCmd(s: String): Option[ConsoleCmd] = {
    regCmds.find(cmd => s.startsWith(cmd.cmd));
  }

  def execute(cmd: String) {
    cmdHistory.putCMdToHistory(cmd);

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
        logHistory.histUp();
        cmd;
      };
      case KEY_NEXT => {
        logHistory.histDown();
        cmd;
      };
      case KEY_RETURN => {
        log(cmd);
        execute(cmd.trim);
        ""
      };
      case KEY_DOWN => {
        cmdHistory.historyPosDown(cmd);
      };
      case KEY_UP => {
        cmdHistory.historyPosUp(cmd);
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

  def draw(){
      drawRectangle();
      drawHistText();
      drawCmd();
  }

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

  def drawHistText(){
    val historyText = logHistory.getHistoryText;

    def recur(txt: Queue[String], yidx: Int){
      if (txt.isEmpty || yidx < -LINE_H) return;

      val elem = txt.dequeue;
      FontManager.drawText(2,yidx,elem._1, consoleHistFontName );
      recur(elem._2, yidx - FONT_HEG)
    }

    recur(historyText.reverse.drop(logHistory.getHistPosition), textLinesCount * LINE_H);

  }

  private def drawCmd() {
     FontManager.drawText(2,height-FONT_HEG,cmd+'_', "redfont" );
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


