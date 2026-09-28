package com.code4galaxy.lingoleap

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.code4galaxy.lingoleap.data.firebase.FirebaseProgressBackup

@HiltAndroidApp
class LingoLeapApplication : Application() {
    @Inject lateinit var firebaseProgressBackup: FirebaseProgressBackup

    override fun onCreate() {
        super.onCreate()
        firebaseProgressBackup.start()
    }
}
