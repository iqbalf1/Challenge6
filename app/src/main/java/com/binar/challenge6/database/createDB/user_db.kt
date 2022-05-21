package com.binar.chapter5.database.createDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.binar.chapter5.database.dao.user_dao
import com.binar.chapter5.database.modelDB.User


@Database(entities = [User::class], version = 4)
abstract class UserDatabase : RoomDatabase() {

    abstract fun UserDao(): user_dao


    companion object {
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase? {
            if (INSTANCE == null) {
                synchronized(User::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "User.db*"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}