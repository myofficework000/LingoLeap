package com.code4galaxy.vaaniverse4u

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.code4galaxy.vaaniverse4u.data.firebase.FirebaseProgressBackup

@HiltAndroidApp
class VaaniVerse4UApplication : Application() {
    @Inject lateinit var firebaseProgressBackup: FirebaseProgressBackup

    override fun onCreate() {
        super.onCreate()
        firebaseProgressBackup.start()
    }
}
