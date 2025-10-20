package com.ichinweze.flickpick.data.persistent.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ichinweze.flickpick.data.ScreenData.LoginDetails

@Entity(tableName = "login_details")
data class LocalLoginDetails(
    @PrimaryKey val email: String,
    val password: String,
    val activeUser: Boolean
)

fun LocalLoginDetails.toExternal() = LoginDetails(
    email = email,
    password = password,
    activeUser = activeUser
)

fun LoginDetails.toLocal() = LocalLoginDetails(
    email = email,
    password = password,
    activeUser = activeUser
)
