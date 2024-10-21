package controller.view

import controller.model.Tasks
import controller.util.DateUtil
import scalafx.scene.control.{Alert, TextField}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import controller.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class TasksEditDialogController (
                                   private val dateField : TextField,
                                   private val taskField : TextField,
                                   private val descriptionField : TextField,
                                   private val statusField : TextField,
                                 ){
  var dialogStage : Stage  = null
  private var _task : Tasks = null
  var okClicked = false

  def task = _task
  def task_=(x : Tasks) {
    _task = x

    dateField.text = _task.date.value.asString
    dateField.setPromptText(DateUtil.DATE_PATTERN);
    taskField.text = _task.task.value
    descriptionField.text  = _task.description.value
    statusField.text = _task.status.value
  }

  def handleCreate(action :ActionEvent){

    if (isInputValid()) {
      _task.date.value = dateField.text.value.parseLocalDate;
      _task.task.value = taskField.text.value
      _task.description.value  = descriptionField.text.value
      _task.status.value    = statusField.text.value

      okClicked = true;
      dialogStage.close()
    }
  }

  def handleCancel(action :ActionEvent) {
    dialogStage.close();
  }
  def nullChecking (x : String) = x == null || x.length == 0

  def isInputValid() : Boolean = {
    var errorMessage = ""

    if (nullChecking(taskField.text.value))
      errorMessage += "Please enter a task.\n"
    if (nullChecking(descriptionField.text.value))
      errorMessage += "Please enter the description of the task.\n"
    if (nullChecking(statusField.text.value))
      errorMessage += "Please enter the completion status of the task.\n"

    else {
      if (!dateField.text.value.isValid) {
        errorMessage += "Please enter the date with the format dd/mm/yyyy.\n";
      }
    }

    if (errorMessage.length() == 0) {
      return true;
    } else {
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please fill in with correct format."
        contentText = errorMessage
      }.showAndWait()

      return false;
    }
  }
}