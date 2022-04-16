package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime (@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String ="",
                  var date: Date = Date(),
                  var isSolved: Boolean = false,
                  var suspect: String = "") {

    val photoFileName
        get() = "IMG_$id.jpg" // photoFileName doesn't have path to folder w/ photo but it will be unique because based on ID

}