package com.binar.chapter5.database.dao

import androidx.room.*
import com.binar.chapter5.database.modelDB.User

@Dao
interface user_dao {

    @Query("SELECT username From User Where username=(:user) AND password=(:pass)")
    fun login(user:String,pass:String) : String

    @Query("Select id From User Where username=(:username)")
    fun getId(username:String) : Int

    @Query("Select images From User Where username=(:username)")
    fun getPhotoProfile(username:String): String

    @Query("SELECT * From User Where id=(:id)")
    fun getUser(id:Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user : User): Long

    @Query("UPDATE User SET images=:images Where id=:id")
    fun updateImages(id:Int, images:String): Int

    @Query("UPDATE User SET username=:username,email=:email,images=:images Where id=:id")
    fun updateUserProfile(id:Int, username: String,email:String,images:String): Int

    @Update
    fun updateUser(user: User):Int

}