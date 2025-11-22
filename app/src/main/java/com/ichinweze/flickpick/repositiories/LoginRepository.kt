package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.data.ScreenData.LoginDetails
import com.ichinweze.flickpick.data.persistent.db.FlickPickDatabase
import com.ichinweze.flickpick.data.persistent.db.local.toLocal
import kotlin.math.log

class LoginRepository(context: Context) {

    private val loginDao = FlickPickDatabase
        .getDatabase(context)
        .loginDao()

    suspend fun findActiveUser(): Boolean {
        return loginDao.findActiveUser() != null
    }

    suspend fun getActiveUserEmail(): String {
        val activeUser = loginDao.findActiveUser()
        return if (activeUser != null) activeUser.email else ""
    }

    suspend fun doesEmailExist(email: String): Boolean {
        return loginDao.checkForEmail(email) != null
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return if (loginDao.getUserCredentials(email, password) != null) {
            loginDao.loginUserWithCredentials(email, password) > 0
        } else false
    }

    suspend fun findUser(email: String, password: String): Boolean {
        return loginDao.getUserCredentials(email, password) != null
    }

    suspend fun createUser(loginDetails: LoginDetails): String {
        val newLogin = loginDetails.toLocal()
        loginDao.upsert(newLogin)
        return newLogin.email
    }
}