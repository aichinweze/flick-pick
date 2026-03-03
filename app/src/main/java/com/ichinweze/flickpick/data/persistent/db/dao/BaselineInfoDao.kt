package com.ichinweze.flickpick.data.persistent.db.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.ichinweze.flickpick.data.persistent.db.local.LocalBaselineDetails

@Dao
interface BaselineInfoDao {
    @Upsert
    suspend fun upsert(baselineDetails: LocalBaselineDetails): Long
}