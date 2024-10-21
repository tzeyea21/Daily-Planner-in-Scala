package controller.view
import controller.model.Tasks
import controller.MainApp
import scalafx.scene.control.{Alert, ButtonType, Label, TableColumn, TableView}
import scalafxml.core.macros.sfxml
import controller.util.DateUtil._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType

import scala.util.{Failure, Success}

@sfxml
class TasksOverviewController(
                               private val tasksTable :TableView[Tasks],
                               private val toDoTasksColumn : TableColumn[Tasks, String],
                               private val statusColumn : TableColumn[Tasks, String],
                               private val dateLabel : Label,
                               private val taskLabel : Label,
                               private val descriptionLabel : Label,
                               private val statusLabel : Label,
                             ) {
  tasksTable.items = MainApp.tasksData
  toDoTasksColumn.cellValueFactory = {_.value.task}
  statusColumn.cellValueFactory  = {_.value.status}

  private def showTasksDetails(task: Option[Tasks]) = {
    task match {
      case Some(t) =>
        dateLabel.text = t.date.value.asString
        taskLabel.text <== t.task
        descriptionLabel.text <== t.description
        statusLabel.text <== t.status

      case None =>
        taskLabel.text.unbind()
        descriptionLabel.text.unbind()
        statusLabel.text.unbind()
        dateLabel.text = ""
        taskLabel.text = ""
        descriptionLabel.text = ""
        statusLabel.text = ""
    }
  }

  showTasksDetails(None)

  tasksTable.selectionModel.value.selectedItem.onChange(
    (x, y, z) => {
      showTasksDetails(Option(z))
    }
  )

  def handleDeleteTask(action: ActionEvent): Unit = {
    val selectedIndex = tasksTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      val task = tasksTable.selectionModel().selectedItem.value

      val confirmationAlert = new Alert(AlertType.Confirmation) {
        title = "Confirmation"
        headerText = "You have selected a task to delete."
        contentText = "Are you sure you want to delete the task?"
      }

      val confirmationResult = confirmationAlert.showAndWait()
      if (confirmationResult.contains(ButtonType.OK)) {
        task.delete () match{
          case Success (x) =>
            MainApp.tasksData.remove(selectedIndex)
            showTasksDetails(None)
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database problem filed to save changes"
            }.showAndWait()
        }

      }
    } else {
      val warningAlert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No task is selected to be delete."
        contentText = "Please select a task."
      }
      warningAlert.showAndWait()
    }
  }

  def handleNewTask(action: ActionEvent) = {
    val task = new Tasks("", "")
    val okClicked = MainApp.showTasksEditDialog(task);
    if (okClicked) {
      //MainApp.tasksData += task
      task.save () match {
      case Success (x) =>
      MainApp.tasksData += task
      case Failure (e) =>
      val alert = new Alert (Alert.AlertType.Warning) {
      initOwner (MainApp.stage)
      title = "Failed to Save"
      headerText = "Database Error"
      contentText = "Database problem filed to save changes"
      }.showAndWait ()
    }
    }
  }

  def handleEditTask(action: ActionEvent) = {
    val selectedTask = tasksTable.selectionModel().selectedItem.value
    if (selectedTask != null) {
      val okClicked = MainApp.showTasksEditDialog(selectedTask)

      if (okClicked) {
        selectedTask.save() match {
          case Success(x) =>
            showTasksDetails(Some(selectedTask))
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database problem filed to save changes"
            }.showAndWait()
      }

    } else {
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No task is selected."
        contentText = "Please select a task."
      }.showAndWait()
    }
  }}
    def handleHomePage(action: ActionEvent): Unit = {
      MainApp.showWelcome(-1)
      }
    }