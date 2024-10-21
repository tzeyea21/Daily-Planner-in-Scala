package controller.view

import controller.MainApp
import scalafx.scene.control.Button
import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController(
  val getStartedToWriteDiaryButton: Button
  ) {
  def getStartedToWriteTask(): Unit = {
    MainApp.showTasksOverview()
  }

  def getStartedToWriteDiary(): Unit = {
    MainApp.showDiaryOverview()
  }
}
