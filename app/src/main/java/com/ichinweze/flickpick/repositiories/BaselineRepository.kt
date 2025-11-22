package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.data.ScreenData.BaselineDetails
import com.ichinweze.flickpick.data.persistent.db.FlickPickDatabase
import com.ichinweze.flickpick.data.persistent.db.local.toLocal

class BaselineRepository(context: Context) {

    private val baselineInfoDao = FlickPickDatabase
        .getDatabase(context)
        .baselineInfoDao()

    suspend fun upsertBaselineDetails(email: String, baselineDetails: BaselineDetails): Boolean {
        val newDetails = baselineDetails.toLocal(email)
        return baselineInfoDao.upsert(newDetails) > 0
    }
}