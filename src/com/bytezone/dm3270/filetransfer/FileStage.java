package com.bytezone.dm3270.filetransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.bytezone.dm3270.application.WindowSaver;

public class FileStage extends Stage
{
  private final TabPane tabPane = new TabPane ();
  private final List<Transfer> transfers = new ArrayList<> ();
  private final Preferences prefs;
  private final WindowSaver windowSaver;
  private final Button hideButton = new Button ("Hide Window");

  private final Label lblLineSize = new Label ("Line size");
  private final Label lblPageSize = new Label ("Page size");
  private final TextField txtLineSize = new TextField ();
  private final TextField txtPageSize = new TextField ();

  public FileStage (Preferences prefs)
  {
    this.prefs = prefs;
    setTitle ("Report display");
    windowSaver = new WindowSaver (prefs, this, "FileTransferStage");

    tabPane.setSide (Side.TOP);
    tabPane.setTabClosingPolicy (TabClosingPolicy.UNAVAILABLE);
    tabPane.setPrefSize (500, 500);           // width, height

    HBox buttonBox = new HBox ();
    hideButton.setPrefWidth (150);
    buttonBox.setAlignment (Pos.CENTER_RIGHT);
    buttonBox.setPadding (new Insets (10, 10, 10, 10));         // trbl
    buttonBox.getChildren ().add (hideButton);

    HBox optionsBox = new HBox (10);
    optionsBox.setAlignment (Pos.CENTER_LEFT);
    optionsBox.setPadding (new Insets (10, 10, 10, 10));         // trbl
    txtPageSize.setPrefWidth (60);
    txtLineSize.setPrefWidth (60);
    optionsBox.getChildren ().addAll (lblPageSize, txtPageSize, lblLineSize, txtLineSize);

    BorderPane bottomBorderPane = new BorderPane ();
    bottomBorderPane.setLeft (optionsBox);
    bottomBorderPane.setRight (buttonBox);

    hideButton.setOnAction (e -> {
      closeWindow ();
      hide ();
    });

    BorderPane borderPane = new BorderPane ();
    borderPane.setCenter (tabPane);
    borderPane.setBottom (bottomBorderPane);

    Scene scene = new Scene (borderPane);
    setScene (scene);

    if (!windowSaver.restoreWindow ())
      centerOnScreen ();

    setOnCloseRequest (e -> closeWindow ());
  }

  public void addTransfer (Transfer transfer)
  {
    if (transfer.isMessage ())
      return;

    transfers.add (transfer);

    Tab tab = new Tab ();
    tab.setText ("#" + transfers.size ());

    TextArea textArea = new TextArea ();
    textArea.setEditable (false);
    textArea.setFont (Font.font ("Monospaced", 12));

    if (transfer.isData ())
    {
      byte[] fullBuffer = transfer.getAllDataBuffers ();
      int lineSize = (fullBuffer.length % 132 == 0) ? 132 : 80;

      LinePrinter linePrinter = new LinePrinter (66, lineSize == 132);
      linePrinter.printBuffer (fullBuffer, lineSize);
      textArea.appendText ("\n");
      textArea.appendText (linePrinter.getOutput ());
    }
    else
    {
      String message = new String (transfer.messageBuffers.get (0).getBuffer ());
      textArea.setText (message);
    }

    tab.setContent (textArea);
    textArea.positionCaret (0);

    Platform.runLater ( () -> tabPane.getTabs ().add (tab));
  }

  private void closeWindow ()
  {
    windowSaver.saveWindow ();
  }
}