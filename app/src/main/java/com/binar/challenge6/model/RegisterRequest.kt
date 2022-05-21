package com.binar.chapter5.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("role")
    val role: String? = null,
): Parcelable