package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.data.ScreenData.AccountDetails
import com.ichinweze.flickpick.data.persistent.db.FlickPickDatabase
import com.ichinweze.flickpick.data.persistent.db.local.toExternal
import com.ichinweze.flickpick.data.persistent.db.local.toLocal

class AccountRepository(context: Context) {

    private val accountDao = FlickPickDatabase
        .getDatabase(context)
        .accountInfoDao()

    suspend fun getAccountDetails(email: String): AccountDetails {
        val accountDetailsDb = accountDao.getAccountDetails(email)

        return if (accountDetailsDb != null) {
            accountDetailsDb.toExternal()
        } else {
            AccountDetails(name = "", email = "", age = 0)
        }
    }

    suspend fun updateAccountDetails(email: String, accDetails: AccountDetails): Boolean {
        val localAccDetails = accDetails.toLocal(email)
        return accountDao.upsert(localAccDetails) > 0
    }
}