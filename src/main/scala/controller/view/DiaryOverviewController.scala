package controller.view

import controller.model.Diary
import controller.MainApp
import scalafx.scene.control.{Alert, Label, TableColumn, TableView, TextArea}
import scalafxml.core.macros.sfxml
import controller.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent

import scala.util.{Failure, Success}

@sfxml
class DiaryOverviewController(
                               private val diaryTable : TableView[Diary],
                               private val dateColumn : TableColumn[Diary, String],
                               private val dateLabel: Label,
                               private val diaryTextArea: TextArea,
                             ) {
  diaryTable.items = MainApp.diaryData
  dateColumn.cellValueFactory = {_.value.diaries}

  private def showDiaryDetails(diary: Option[Diary]) = {
    diary match {
      case Some(t) =>
        dateLabel.text = t.date.value.asString

        diaryTextArea.text.unbind()

        if (t.diaries != null) {
          diaryTextArea.text.bind(t.diaries)
        } else {
          diaryTextArea.text.value = ""
        }

      case None =>
        diaryTextArea.text.unbind()
        dateLabel.text = ""
        diaryTextArea.text.value = ""
    }
  }

  showDiaryDetails(None)

  diaryTable.selectionModel.value.selectedItem.onChange(
    (x, y, z) => {
      showDiaryDetails(Option(z))
    }
  )

  def handleNewDiary(action: ActionEvent) = {
    val diary = new Diary("")
    val okClicked = MainApp.showDiaryEditDialog(diary)
    if (okClicked) {
      //MainApp.tasksData += task
      diary.save() match {
        case Success(x) =>
          MainApp.diaryData += diary
        case Failure(e) =>
          val alert = new Alert(Alert.AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database problem filed to save changes"
          }.showAndWait()
      }
  }}

  def handleHomePage(action: ActionEvent): Unit = {
    MainApp.showWelcome(-1)
  }
}