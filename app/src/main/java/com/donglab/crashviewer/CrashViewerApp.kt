package com.donglab.crashviewer

import android.app.Application
import android.content.Context
import android.health.connect.datatypes.AppInfo
import com.donglab.crash.publicapi.dsl.installCrashHandler
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.impl.AppInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection

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