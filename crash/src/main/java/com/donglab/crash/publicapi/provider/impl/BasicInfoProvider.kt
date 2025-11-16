package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 기본 크래시 정보 Provider
 * - 발생 시간
 * - 발생 화면
 */
class BasicInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return CrashInfoSection(
            title = "크래시 정보",
            items = listOf(
                CrashInfoItem("발생 시간", sdf.format(Date(timestamp))),
                CrashInfoItem("발생 화면", activityName)
            )
        )
    }
}
