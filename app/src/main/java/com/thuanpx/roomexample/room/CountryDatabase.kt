package com.thuanpx.roomexample.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thuanpx.roomexample.model.City

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 5/27/20.
 */
@Database(entities = [City::class], version = 1)
abstract class CountryDatabase : RoomDatabase() {

    abstract fun countryDao(): CountryDao

    companion object {
        @Volatile
        private var INSTANCE: CountryDatabase? = null

        fun getInstance(context: Context): CountryDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CountryDatabase::class.java,
                "cities.db"
            )
                .createFromAsset("database/utopia_cities.db")
                .build()
    }
}