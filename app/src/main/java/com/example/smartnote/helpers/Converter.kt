package com.example.smartnote.helpers

import androidx.room.TypeConverter
import java.util.Date

class Converter {
  @TypeConverter
  fun toListOfStrings(flatStringList: String): List<String> {
    return flatStringList.split(",")
  }
  @TypeConverter
  fun fromListOfStrings(listOfString: List<String>): String {
    return listOfString.joinToString(",")
  }
  @TypeConverter
  fun fromTimestamp(value: Long?): Date? {
    return value?.let { Date(it) }
  }

  @TypeConverter
  fun dateToTimestamp(date: Date?): Long? {
    return date?.time
  }
}
