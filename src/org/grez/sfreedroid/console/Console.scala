package org.grez.sfreedroid.console

import org.lwjgl.opengl.GL11._
import org.grez.sfreedroid.font.FontManager
import org.lwjgl.input.Keyboard
import collection.immutable.Queue
import org.grez.sfreedroid.drawable.Drawable

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

  def putHistory(line: String,  additor: String = "") {

    def putTx(txt: String){
      historyText += additor+txt;
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


class Console(val height:Int, val histSize:Int, val regCmds: List[ConsoleCmd]) extends Drawable {

  val FONT_HEG = 25;
  val LINE_H = (FONT_HEG+4);
  val textLinesCount: Int = (height / LINE_H)-1;

  //var defaultConsoleFont = "font05" ;

  var showing = false
  var cmd: String = "";

  val logHistory = new ConsoleLogHistory(histSize, textLinesCount);
  val cmdHistory = new ConsoleCMDHistory(histSize);

  val logFontIndex = scala.collection.mutable.Map ((0,"font05"),(1,"font05_red"),(2,"font05_yellow")); //todo redo this font config thing!


  def log (value : Any){
    println(value);
    logHistory.putHistory(value.toString);
  }

  def logFromCommand (value : Any){
    log(value,1);
  }

  def log (value : Any, cidx: Int){
    println(value);
    if (value.toString().startsWith("%")){
      logHistory.putHistory(value.toString);
    } else
    logHistory.putHistory(value.toString, "%"+cidx);
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

  def searchStrCMD(s:String): List[ConsoleCmd] = {
    regCmds.filter(cmd => cmd.cmd.startsWith(s));
  }

  def searchCmdStrict(s: String): Option[ConsoleCmd] = {
    regCmds.find(cmd => s.equals(cmd.cmd));
  }

  def autoAppendToCommon(l: List[ConsoleCmd]): String = {
    autoAppendToCommonStr(l.map(c => c.cmd));
  }

  def autoAppendToCommonStr(l: List[String]): String = {
    def fidex (i: Int): Boolean = {
      val letter = l(0).charAt(i);
      for {j <- 1 until l.size } {
        if (i >= l(j).size  || letter != l(j).charAt(i)) return false;
      }
      true;
    }

    var i = 0;
    while (fidex(i)){
      i += 1;
    }

    l(0).take(i);
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
        log(cmd,2);
        execute(cmd.trim);
        ""
      };
      case KEY_DOWN => {
        cmdHistory.historyPosDown(cmd);
      };
      case KEY_UP => {
        cmdHistory.historyPosUp(cmd);
      };
      case KEY_TAB => {
        processTab(cmd)
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
        case pc if FontManager.printableChar(pc) => cmd+c;
        case _ => cmd;
      };
    }
  }

  private def processPrimaryTab(cmd_s: String): String = {
      //first level auto ident!
            val foundCmds = searchStrCMD(cmd_s);
            foundCmds match {
              case l if l.isEmpty => cmd_s;
              case l if l.size == 1 => l(0).cmd;
              case l => {
                log(l.foldLeft("possible cmds:\n")((st, curr) => st + "\t" + curr.cmd + "\n"))
                autoAppendToCommon(l);
              }
            }
  }

  private def processSecondaryTab(cmd_s: String, args: Array[String]): String ={

    val firstLevelCMD = searchCmdStrict(args(0))
    val paramsArg  =  { //args list without cmd itself, and spaces.
     val tArgs = args.tail.filter(s => !s.isEmpty)
       if (cmd_s.last == ' ') tArgs :+ "" else tArgs;
    };

    val pArg = paramsArg.last;
    val idxArg = paramsArg.size -1;

    firstLevelCMD match {
      case None => cmd_s; //first argument is not a cmd!
      case l: Some[ConsoleCmd] if l.get.params.isEmpty || l.get.params.get.size -1 < idxArg => cmd_s; //params list is empty, or doesn't fit
      case l: Some[ConsoleCmd] if l.get.params.get(idxArg).autoidentProvider.isEmpty => cmd_s; //param doesn't contain autoidentProvider
      case l => {

        val paramsList = l.get.params.get(idxArg).autoidentProvider.get.getStrList;
        val searchParamsList = if(pArg.isEmpty) paramsList else paramsList.filter(str => str.startsWith(pArg))

        def foldParams: String = {
          args.head +" " + paramsArg.init.foldLeft("")((s, a) => s + a + " ")
        }

        searchParamsList match {
          case ll if ll.isEmpty => cmd_s;
          case ll if ll.size == 1 => foldParams + ll(0);
          case ll => {
            log(ll.foldLeft("possible cmds:\n")((st, curr) => st + "\t" + curr + "\n"));
            foldParams + autoAppendToCommonStr(ll);
          }
        }
      }
    }
  }

  private def processTab(cmd_s: String): String = {
    val args = cmd_s.split(' ');

    if (args.size <= 0) return cmd_s;
    if (args.size == 1 && (cmd.isEmpty || cmd_s.last != ' ')) processPrimaryTab(cmd_s) else processSecondaryTab(cmd_s, args)
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
      val line = elem._1;
      val defaultFontName = logFontIndex.get(0).get;

       if (!line.isEmpty && line(0) == '%'){
         val fontIdx = Integer.parseInt(line.substring(1,2)) ;
         val fontName = logFontIndex.get(fontIdx) match {
           case s: Some[String] => s.get;
           case None => defaultFontName;
         }

         FontManager.drawText(2,yidx,line.drop(2), fontName);
       } else {
         FontManager.drawText(2,yidx,line, defaultFontName);
       }
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

abstract class SecondLevelAutoidentListProvider {
  def getStrList: List[String];
}

object AutoIdentList {
  def apply(xs: String*): Some[SecondLevelAutoidentListProvider] = {
    Some (new SecondLevelAutoidentListProvider {
      def getStrList = xs.toList;
    });
  }
}

import CmdParamType._; //wierd scala enums

case class CmdParam(name: String, paramType: CmdParamType, help: String, autoidentProvider: Option[SecondLevelAutoidentListProvider] = None);

object CmdParamsList {
  def apply(xs: CmdParam*):Option[List[CmdParam]] = {
    Some(xs.toList);
  }
  /*def apply(name: String, paramType: CmdParamType, help: String, autoidentProvider: Option[SecondLevelAutoidentListProvider] = None): Option[CmdParam] = {
    Some(CmdParam(name,paramType,help,autoidentProvider));
  }*/
}

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


