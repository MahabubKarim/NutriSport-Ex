package com.mmk.nutrisport

import android.app.Application
import com.google.firebase.FirebaseApp
import com.nutrisport.core.AppContextHolder
import com.nutrisport.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppContextHolder.appContext = this
        initializeKoin(
            config = {
                // Allow Koin Library to use Android context and
                // inject it later in some of our dependencies that might depend on android context
                androidContext(this@MyApplication)
            }
        )
        FirebaseApp.initializeApp(this)
    }
}