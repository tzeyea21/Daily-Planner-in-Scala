package controller

import controller.model.{Diary, Tasks}
import controller.util.Database
import controller.view.{DiaryEditDialogController, TasksEditDialogController, WelcomeController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.stage.{Modality, Stage}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.Includes._

object MainApp extends JFXApp {
  Database.setupDB()

  val tasksData = new ObservableBuffer[Tasks]()
  tasksData ++= Tasks.getAllTasks

  val diaryData = new ObservableBuffer[Diary]()
  diaryData ++= Diary.getAllDiaries

  val rootResource = getClass.getResource("view/RootLayout.fxml")

  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()

  val roots = loader.getRoot[javafx.scene.layout.BorderPane]
  stage = new PrimaryStage {
    title = "Daily Planner"
    scene = new Scene {
      root = roots
    }
  }

  def showTasksOverview(): Unit = {
    val resource = getClass.getResource("view/TasksOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showDiaryOverview(): Unit = {
    val resource = getClass.getResource("view/DiaryOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.BorderPane]
    this.roots.setCenter(roots)
  }

  def showWelcome(selectedOption: Int): Unit = {
    val resource = selectedOption match {
      case 1 => getClass.getResource("view/TasksOverview.fxml")
      case 2 => getClass.getResource("view/DiaryOverview.fxml")
      case _ => getClass.getResource("view/Welcome.fxml") // Default option
    }

    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)

    selectedOption match {
      case 2 =>
        val controller = loader.getController[WelcomeController]
        controller.getStartedToWriteDiaryButton.setOnAction(_ => showDiaryOverview())
      case _ =>
    }
  }

    def showTasksEditDialog(tasks: Tasks): Boolean = {
      val resource = getClass.getResourceAsStream("view/TasksEditDialog.fxml")
      val loader = new FXMLLoader(null, NoDependencyResolver)
      loader.load(resource)
      val roots2 = loader.getRoot[jfxs.Parent]
      val control = loader.getController[TasksEditDialogController#Controller]

      val dialog = new Stage() {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(stage)
        scene = new Scene {
          root = roots2
        }
      }
      control.dialogStage = dialog
      control.task = tasks
      dialog.showAndWait()
      control.okClicked
    }

  def showDiaryEditDialog(diary: Diary): Boolean = {
    val resource = getClass.getResourceAsStream("view/DiaryEditDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[DiaryEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.diaries = diary
    dialog.showAndWait()
    control.okClicked
  }
  showWelcome(-1)
}
