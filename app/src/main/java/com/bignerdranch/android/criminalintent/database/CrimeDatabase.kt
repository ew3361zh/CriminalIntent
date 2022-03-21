package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent.Crime

@Database(entities = [ Crime::class ], version=1)
@TypeConverters(CrimeTypeConverters::class) // tell db to use functions in this class when converting types
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao // register Dao with DB class, Room will handle generating concrete version of class
}