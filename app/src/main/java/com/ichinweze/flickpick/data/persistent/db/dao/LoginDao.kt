package com.ichinweze.flickpick.data.persistent.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.ichinweze.flickpick.data.persistent.db.local.LocalLoginDetails

@Dao
interface LoginDao {
    @Upsert
    suspend fun upsert(loginDetails: LocalLoginDetails)

    @Delete
    suspend fun deleteLoginDetails(loginDetails: LocalLoginDetails)

    @Query("SELECT * FROM login_details WHERE activeUser = 1")
    suspend fun findActiveUser(): List<LocalLoginDetails>

    @Query("SELECT * FROM login_details WHERE email = :email LIMIT 1")
    suspend fun checkForEmail(email: String): LocalLoginDetails?
}