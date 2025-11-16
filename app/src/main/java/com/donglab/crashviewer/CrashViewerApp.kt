package com.donglab.crashviewer

import android.app.Application
import com.donglab.crash.dsl.installCrashHandler

class CrashViewerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Install crash handler with default providers
        installCrashHandler(this) {
            providers {
                useDefault()
            }
        }
    }
}
