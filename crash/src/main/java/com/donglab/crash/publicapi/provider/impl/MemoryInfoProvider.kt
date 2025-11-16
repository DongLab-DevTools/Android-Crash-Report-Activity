package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.SectionType

/**
 * 메모리 정보 Provider
 * - 사용 중인 메모리
 * - 전체 메모리
 * - 최대 메모리
 * - 여유 메모리
 */
class MemoryInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / 1024 / 1024
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory

        return CrashInfoSection(
            title = "메모리 정보",
            items = listOf(
                CrashInfoItem("Used", "$usedMemory MB"),
                CrashInfoItem("Total", "$totalMemory MB"),
                CrashInfoItem("Max", "$maxMemory MB"),
                CrashInfoItem("Free", "$freeMemory MB")
            ),
            type = SectionType.CODE
        )
    }
}
