package com.ichinweze.flickpick.data.persistent.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ichinweze.flickpick.data.persistent.db.local.LocalAccountDetails

@Dao
interface AccountInfoDao {
    @Query("SELECT * FROM `account-details` WHERE email = :email LIMIT 1")
    suspend fun getAccountDetails(email: String): LocalAccountDetails?

    @Upsert
    suspend fun upsert(localAccountDetails: LocalAccountDetails): Long
}