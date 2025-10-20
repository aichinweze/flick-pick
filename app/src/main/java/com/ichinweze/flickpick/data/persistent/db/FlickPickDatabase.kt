package com.ichinweze.flickpick.data.persistent.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ichinweze.flickpick.data.persistent.db.dao.LoginDao
import com.ichinweze.flickpick.data.persistent.db.dao.AccountInfoDao
import com.ichinweze.flickpick.data.persistent.db.dao.BaselineInfoDao
import com.ichinweze.flickpick.data.persistent.db.dao.HistoricalInfoDao
import com.ichinweze.flickpick.data.persistent.db.local.LocalAccountDetails
import com.ichinweze.flickpick.data.persistent.db.local.LocalBaselineDetails
import com.ichinweze.flickpick.data.persistent.db.local.LocalHistoricalDetails
import com.ichinweze.flickpick.data.persistent.db.local.LocalLoginDetails

@Database(
    entities = [
        LocalLoginDetails::class,
        LocalAccountDetails::class,
        LocalHistoricalDetails::class,
        LocalBaselineDetails::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FlickPickDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao

    abstract fun accountInfoDao(): AccountInfoDao

    abstract fun baselineInfoDao(): BaselineInfoDao

    abstract fun historicalInfoDao(): HistoricalInfoDao

    companion object {
        @Volatile
        private var INSTANCE: FlickPickDatabase? = null

        fun getDatabase(@ApplicationContext context: Context): FlickPickDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context,
                    FlickPickDatabase::class.java,
                    "flick-pick-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}