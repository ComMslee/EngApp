package com.litbig.engapp

import android.app.Application
import com.litbig.engapp.utils.TestManager
import org.koin.core.context.startKoin
import org.koin.dsl.module

class EngApplication : Application() {
    val appModule = module {
        single { TestManager() }
    }
    override fun onCreate() {
        super.onCreate()
        startKoin { appModule }
    }
}