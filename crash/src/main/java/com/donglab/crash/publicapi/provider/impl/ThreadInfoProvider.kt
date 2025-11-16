package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import android.os.Looper
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.SectionType

/**
 * 스레드 정보 Provider
 * - 스레드 이름
 * - 메인 스레드 여부
 */
class ThreadInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection {
        val isMainThread = Looper.myLooper() == Looper.getMainLooper()

        return CrashInfoSection(
            title = "스레드 정보",
            items = listOf(
                CrashInfoItem("Thread", thread.name),
                CrashInfoItem("Is Main Thread", isMainThread.toString())
            ),
            type = SectionType.CODE
        )
    }

    override fun getOrder(): Int = 20
}
