package com.binar.chapter5.data_store

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.map


class UserManager(context: Context) {
    private val dataStore = context.createDataStore(name = "user_prefs")

    companion object{
        val USERNAME_KEY = preferencesKey<String>("USERNAME")
        val IS_LOGGED_IN_KEY = preferencesKey<Boolean>("IS_LOGGED_IN")
    }
    suspend fun storePrefs(username:String,isLoggedIn : Boolean){
        dataStore.edit {
            it[USERNAME_KEY] = username
            it[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }
    val userNameFlow : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map{
        it[USERNAME_KEY] ?: ""
    }
    val isLoggedInFlow : kotlinx.coroutines.flow.Flow<Boolean> = dataStore.data.map{
        it[IS_LOGGED_IN_KEY] ?: false
    }
}