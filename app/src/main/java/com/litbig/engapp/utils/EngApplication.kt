package com.litbig.engapp.utils

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class EngApplication : Application() {
    val appModule = module {
        single { TestManager() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            printLogger(Level.NONE)
            androidContext(this@EngApplication)
            modules(appModule)
        }
    }
}