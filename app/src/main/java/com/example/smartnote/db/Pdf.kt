package com.example.smartnote.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "pdf_locations_table")
data class Pdf(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="pdf_id")
  val id:Int,
  @ColumnInfo(name = "pdf_name")
  val name: String,
  @ColumnInfo(name = "pdf_location")
  val location:String,
  @ColumnInfo(name = "pdf_upload_time")
  val time: Date

)
