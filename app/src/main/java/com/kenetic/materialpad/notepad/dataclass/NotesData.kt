package com.kenetic.materialpad.notepad.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_data")
data class NotesData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "notes") var notes: List<Notes>,
    //adding new variables--------------------------------------------------------------------------
    @ColumnInfo(name = "is_favourite") var isFavourite: Boolean,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date_formatted")var dateFormatted:Long
)