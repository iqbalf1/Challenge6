package com.binar.chapter5.model

import com.google.gson.annotations.SerializedName

data class RegisterResponseItem(
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)