package controller.view

import controller.model.Diary
import controller.util.DateUtil
import scalafx.scene.control.{Alert, TextField}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import controller.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class DiaryEditDialogController(
                       private val dateField: TextField,
                       private val diaryField: TextField,
                     ) {

  var dialogStage: Stage = null
  private var _diaries: Diary = null
  var okClicked = false

  def diaries = _diaries
  def diaries_=(x: Diary) {
    _diaries = x

    dateField.text = _diaries.date.value.asString
    dateField.setPromptText(DateUtil.DATE_PATTERN);
    diaryField.text = _diaries.diaries.value
  }

    def handleCreate(action: ActionEvent) {
      if (isInputValid()) {
        _diaries.date.value = dateField.text.value.parseLocalDate;
        _diaries.diaries.value = diaryField.text.value

        okClicked = true;
        dialogStage.close()
      }
    }

  def handleCancel(action: ActionEvent) {
    dialogStage.close();
  }
      def nullChecking(x: String) = x == null || x.length == 0

      def isInputValid(): Boolean = {
        var errorMessage = ""

        if (nullChecking(diaryField.text.value))
          errorMessage += "The diary is empty.\n"

        else {
          if (!dateField.text.value.isValid) {
            errorMessage += "Please enter the date with the format dd/mm/yyyy.\n";
          }
        }

        if (errorMessage.length() == 0) {
          return true;
        } else {
          // Show the error message.
          val alert = new Alert(Alert.AlertType.Error) {
            initOwner(dialogStage)
            title = "Invalid Fields"
            headerText = "Please fill in with correct format."
            contentText = errorMessage
          }.showAndWait()
          return false;
        }
      }
}
