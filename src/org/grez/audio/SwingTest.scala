package org.grez.audio

/**
 * Created by IntelliJ IDEA.
 * User: grez
 * Date: Sep 18, 2010
 * Time: 10:45:46 PM
 * Swing test
 */

import swing._
import event.ButtonClicked
import java.awt.Color
import java.io.{FileInputStream, File}
import com.trb.sound.{OggInputStream, OggPlayer}
import org.lwjgl.openal.AL

object SwingTest extends SimpleSwingApplication{

  def flist():List[String] =  new File("./music").list().toList;

   val player: OggPlayer = new OggPlayer();

  val playButton:Button  =  Button("Play"){
 //     Dialog.showMessage(buttonsPanel,"Play action");
      playButton.enabled = false;
      stopButton.enabled = true;
      val loadFile: File = new File("./music/"+lv.selection.items(0));
      println(loadFile.exists +" "+ loadFile.getName);
      val is: OggInputStream = new OggInputStream(new FileInputStream(loadFile));

      player.open(is);
      player.playInNewThread(5);
  }

  val stopButton:Button =  new Button(){
    text = "Stop";
    enabled = false;
    reactions += {
      case e: ButtonClicked =>
//         Dialog.showMessage(buttonsPanel,"Stop Action","title");
        player.stopPlayInNewThread();
        playButton.enabled = true;
         stopButton.enabled = false;
      case o: Any =>
         println(o);
    }
  }

  val buttonsPanel = new FlowPanel(playButton,Swing.HStrut(60) ,stopButton );

  val lv = new ListView(flist()) {
    import swing.ListView.IntervalMode;
    background = Color.cyan;
    selection.intervalMode = IntervalMode.Single;
  };

  val sclv = new ScrollPane(lv);

   val ui: Panel = new BorderPanel {
    layout(sclv) = BorderPanel.Position.Center
    layout(buttonsPanel) = BorderPanel.Position.South
  }

  def top = new MainFrame {
    title = "Test test :-) ";
    contents = ui;
  }
  AL.create();
}