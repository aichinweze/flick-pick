package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.data.ScreenData.LoginDetails
import com.ichinweze.flickpick.data.persistent.db.FlickPickDatabase
import com.ichinweze.flickpick.data.persistent.db.local.toLocal

class LoginRepository(context: Context) {

    private val loginDao = FlickPickDatabase
        .getDatabase(context)
        .loginDao()

    suspend fun findActiveUser(): Boolean {
        val activeUser = loginDao.findActiveUser().toList()

        return activeUser.isNotEmpty()
    }

    suspend fun doesEmailExist(email: String): Boolean {
        return loginDao.checkForEmail(email) != null
    }

    suspend fun createUser(loginDetails: LoginDetails): String {
        val newLogin = loginDetails.toLocal()
        loginDao.upsert(newLogin)
        return newLogin.email
    }
}