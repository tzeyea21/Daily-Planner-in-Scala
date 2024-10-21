package controller.model

import controller.util.{Database, DateUtil}
import controller.util.DateUtil.DateFormatter
import scalafx.beans.property.{ObjectProperty, StringProperty}
import java.time.LocalDate
import scalikejdbc.{DB, _}

import scala.util.{Failure, Success, Try}

class Diary ( diaryS: String ) extends Database {
  def this() = this(null)

  var date = ObjectProperty[LocalDate](LocalDate.of(2023, 8, 19))
  var diaries = new StringProperty(diaryS)

  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into diary (date, diaries) values
          (${date.value.asString}, ${diaries.value})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
          update diary
          set
          date  = ${date.value.asString} ,
          diaries   = ${diaries.value}
        """.update.apply()
      })
    }
  }

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
          select * from diary where
          date = ${date.value.asString} and diaries = ${diaries.value}
        """.map(rs => rs.string("date")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }
}}
  object Diary extends Database {
    def apply(
               dateS: String,
               diaryS: String
             ): Diary = {

      new Diary(diaryS) {
        date.value = LocalDate.parse(dateS, DateUtil.DATE_FORMATTER)
      }
    }

    def initializeTable() = {
      if (!hasTableInitialized) {
        DB autoCommit { implicit session =>
          sql"""
             create table diary (
               id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
               date varchar(64),
               diaries varchar(500)
             )
           """.execute.apply()
        }
      }
    }

    def hasTableInitialized: Boolean = {
      DB.getTable("diary") match {
        case Some(_) => true
        case None => false
      }
    }

    def getAllDiaries: List[Diary] = {
      DB readOnly { implicit session =>
        sql"select * from diary".map(rs => Diary(rs.string("date"),
          rs.string("diaries"))).list.apply()
      }
    }
}