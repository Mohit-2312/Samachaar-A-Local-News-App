package com.example.localnewstracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsItemEntity::class], version = 1)
abstract class NewsItemDatabase: RoomDatabase() {
    abstract fun newsItemDao() : NewsItemDao

    companion object {
        @Volatile
        private var INSTANCE: NewsItemDatabase? = null

        fun getDatabase(context: Context): NewsItemDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsItemDatabase::class.java,
                    "news_item_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}