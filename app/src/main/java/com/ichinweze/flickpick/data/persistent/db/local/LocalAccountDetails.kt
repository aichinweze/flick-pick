package com.ichinweze.flickpick.data.persistent.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ichinweze.flickpick.data.ScreenData.AccountDetails

@Entity(tableName = "account-details")
data class LocalAccountDetails(
    @PrimaryKey val email: String,
    val name: String,
    val age: Int?
)

fun LocalAccountDetails.toExternal() = AccountDetails(
    email = email,
    name = name,
    age = age
)

fun AccountDetails.toLocal(email: String) = LocalAccountDetails(
    name = name,
    email = email,
    age = age
)