package com.bytezone.dm3270.assistant;

import com.bytezone.dm3270.display.Field;
import com.bytezone.dm3270.display.ScreenDetails;
import com.bytezone.dm3270.display.TSOCommandStatusListener;
import com.bytezone.dm3270.jobs.BatchJob;
import com.bytezone.dm3270.jobs.JobTable;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

public class JobTab extends Tab implements TSOCommandStatusListener
{
  //  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());
  //  private final WindowSaver windowSaver;
  //  private ConsolePane consolePane;
  //  private final Button btnHide = new Button ("Hide Window");
  private final Button btnExecute;
  private final TextField txtCommand;
  private final JobTable jobTable = new JobTable ();
  //  private final Label lblCommand = new Label ("Command");

  private boolean isTSOCommandScreen;
  private Field tsoCommandField;

  private BatchJob selectedBatchJob;

  public JobTab (TextField text, Button execute)
  {
    super ("Batch Jobs");

    setClosable (false);
    this.txtCommand = text;
    this.btnExecute = execute;

    //    HBox buttonBox = new HBox ();
    //    btnHide.setPrefWidth (120);
    //    buttonBox.setAlignment (Pos.CENTER_RIGHT);
    //    buttonBox.setPadding (new Insets (10, 10, 10, 10));// trbl
    //    buttonBox.getChildren ().add (btnHide);

    //    HBox optionsBox = new HBox (10);
    //    optionsBox.setAlignment (Pos.CENTER_LEFT);
    //    optionsBox.setPadding (new Insets (10, 10, 10, 10));// trbl
    //    txtCommand.setEditable (false);
    //    txtCommand.setPrefWidth (320);
    //    txtCommand.setFont (Font.font ("Monospaced", 12));
    //    txtCommand.setFocusTraversable (false);
    //    optionsBox.getChildren ().addAll (lblCommand, txtCommand, btnExecute);

    //    BorderPane bottomBorderPane = new BorderPane ();
    //    bottomBorderPane.setLeft (optionsBox);
    //    bottomBorderPane.setRight (buttonBox);

    //    btnHide.setOnAction (e -> hide ());
    //    btnExecute.setOnAction (e -> execute ());

    //    BorderPane borderPane = new BorderPane ();
    //    borderPane.setCenter (jobTable);
    //    borderPane.setBottom (bottomBorderPane);

    //    Scene scene = new Scene (borderPane, 500, 500);
    //    setScene (scene);

    //    windowSaver = new WindowSaver (prefs, this, "JobStage");
    //    windowSaver.restoreWindow ();

    //    setOnCloseRequest (e -> closeWindow ());

    jobTable.getSelectionModel ().selectedItemProperty ()
        .addListener ( (obs, oldSelection, newSelection) -> {
          if (newSelection != null)
            select (newSelection);
        });

    setContent (jobTable);
  }

  //  public void setConsolePane (ConsolePane consolePane)
  //  {
  //    this.consolePane = consolePane;
  //  }

  private void select (BatchJob batchJob)
  {
    selectedBatchJob = batchJob;
    setText ();
  }

  private void setText ()
  {
    String report = selectedBatchJob.getOutputFile ();
    String command = report == null ? selectedBatchJob.outputCommand ()
        : String.format ("IND$FILE GET %s", report);

    if (!isTSOCommandScreen)
      command = "TSO " + command;

    txtCommand.setText (command);
    setButton ();
  }

  private void setButton ()
  {
    if (selectedBatchJob == null || selectedBatchJob.getJobCompleted () == null)
    {
      btnExecute.setDisable (true);
      return;
    }

    String command = txtCommand.getText ();
    btnExecute.setDisable (tsoCommandField == null || command.isEmpty ());
  }

  //  private void execute ()
  //  {
  //    if (tsoCommandField != null) // are we on a suitable screen?
  //    {
  //      tsoCommandField.setText (txtCommand.getText ());
  //      if (consolePane != null)
  //        consolePane.sendAID (AIDCommand.AID_ENTER, "ENTR");
  //
  //      if (selectedBatchJob.getOutputFile () == null)
  //      {
  //        selectedBatchJob.setOutputFile (selectedBatchJob.datasetName ());
  //        jobTable.refresh ();
  //      }
  //    }
  //  }

  public void addBatchJob (BatchJob batchJob)
  {
    jobTable.addJob (batchJob);
  }

  public BatchJob getBatchJob (int jobNumber)
  {
    return jobTable.getBatchJob (jobNumber);
  }

  //  private void closeWindow ()
  //  {
  //    windowSaver.saveWindow ();
  //    hide ();
  //  }

  @Override
  public void screenChanged (ScreenDetails screenDetails)
  {
    isTSOCommandScreen = screenDetails.isTSOCommandScreen ();
    tsoCommandField = screenDetails.getTSOCommandField ();
    setButton ();
  }

  // ---------------------------------------------------------------------------------//
  // Batch jobs
  // ---------------------------------------------------------------------------------//

  public void batchJobSubmitted (int jobNumber, String jobName)
  {
    BatchJob batchJob = new BatchJob (jobNumber, jobName);
    addBatchJob (batchJob);
  }

  public void batchJobEnded (int jobNumber, String jobName, String time,
      int conditionCode)
  {
    BatchJob batchJob = getBatchJob (jobNumber);
    if (batchJob != null)
    {
      batchJob.completed (time, conditionCode);
      jobTable.refresh ();// temp fix before jdk 8u60
    }
  }
}