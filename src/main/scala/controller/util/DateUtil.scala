package controller.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

object DateUtil {
  val DATE_PATTERN = "dd/MM/yyyy"
  val DATE_FORMATTER =  DateTimeFormatter.ofPattern(DATE_PATTERN)

  implicit class DateFormatter (val date : LocalDate){

    def asString : String = {
      if (date == null) {
        return null;
      }
      DATE_FORMATTER.format(date);
    }
  }
  implicit class StringFormatter (val data : String) {
    def parseLocalDate : LocalDate = {
      try {
        LocalDate.parse(data, DATE_FORMATTER)
      } catch {
        case  e: DateTimeParseException => null
      }
    }
    def isValid : Boolean = {
      data.parseLocalDate != null
    }
  }
}