package controller.util

import scalikejdbc._
import controller.model.Tasks
import controller.model.Diary

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;";
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

object Database extends Database {
  def setupDB() = {
    if (!hasDBInitialize) {
      Tasks.initializeTable()
      Diary.initializeTable()
    }
  }

  def hasDBInitialize: Boolean = {

    val hasTasksTable = DB.getTable("task").isDefined
    val hasDiaryTable = DB.getTable("diary").isDefined
    hasTasksTable && hasDiaryTable
    }
}