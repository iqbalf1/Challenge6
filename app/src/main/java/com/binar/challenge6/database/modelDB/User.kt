package com.binar.chapter5.database.modelDB

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "images")
    var images:String,
    @ColumnInfo(name = "password")
    var password: String,
    @ColumnInfo(name = "repassword")
    var repassword: String,
) : Parcelable
