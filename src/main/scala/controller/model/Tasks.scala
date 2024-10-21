package controller.model

import controller.util.Database
import controller.util.DateUtil.DateFormatter
import scalafx.beans.property.{ObjectProperty, StringProperty}
import java.time.LocalDate
import scalikejdbc._
import scala.util.{Failure, Success, Try}

class Tasks ( dateS : String, taskS : String ) extends Database {
  def this() = this(null, null)
  var date = ObjectProperty[LocalDate](LocalDate.of(2023, 8, 19))
  var task  = new StringProperty(taskS)
  var description = new StringProperty()
  var status = new StringProperty("Completed")

  def save(): Try[Int] = {
    if (!(isExist)) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into task (date, task, description, status) values
            (${date.value.asString}, ${task.value}, ${description.value}, ${status.value})
        """.update.apply()
    })
  } else {
    Try(DB autoCommit { implicit session =>
      sql"""
        update task
        set
        date  = ${date.value.asString} ,
        task   = ${task.value},
        description = ${description.value},
        status = ${status.value}
        """.update.apply()
    })
  }
}
def delete(): Try[Int] = {
  if (isExist) {
    Try(DB autoCommit { implicit session =>
      sql"""
        delete from task where
          date = ${date.value.asString} and task = ${task.value}
        """.update.apply()
    })
  } else
    throw new Exception("Task not exists in database")
}
  def isExist: Boolean = {
    val count = DB readOnly { implicit session =>
      sql"""
      select count(*) from task where
      date = ${date.value.asString} and task = ${task.value} and description = ${description.value}
    """.map(rs => rs.int(1)).single.apply()
    }

    count.exists(_ > 0)
  }
}

object Tasks extends Database {
  def apply(
             dateS: String,
             taskS: String,
             descriptionS: String,
             statusS: String,
           ): Tasks = {

    new Tasks(dateS, taskS) {
      description.value = descriptionS
      status.value = statusS
    }
  }

  def initializeTable() = {
    if (!hasTableInitialized) {
      DB autoCommit { implicit session =>
        sql"""
      create table task (
        id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        date varchar(64),
        task varchar(64),
        description varchar(300),
        status varchar(64)
      )
      """.execute.apply()
      }
    }}

  def hasTableInitialized: Boolean = {
    DB.getTable("task") match {
      case Some(_) => true
      case None => false
    }
  }

  def getAllTasks: List[Tasks] = {
    DB readOnly { implicit session =>
      sql"select * from task".map(rs => Tasks(rs.string("date"),
        rs.string("task"), rs.string("description"),
        rs.string("status"))).list.apply()
    }
  }
}