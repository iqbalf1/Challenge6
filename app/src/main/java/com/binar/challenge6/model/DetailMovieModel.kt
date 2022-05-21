package com.binar.chapter5.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMovieModel (
    val title :String,
    val subtitle : String,
    val overview : String,
    val date :String,
    val image:String,
    val rating : String,
): Parcelable
