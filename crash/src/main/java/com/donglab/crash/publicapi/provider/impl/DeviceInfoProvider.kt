package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import android.os.Build
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.SectionType

/**
 * 디바이스 정보 Provider
 * - 제조사
 * - 모델명
 * - Android 버전
 * - API 레벨
 */
class DeviceInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection {
        return CrashInfoSection(
            title = "디바이스 정보",
            items = listOf(
                CrashInfoItem("제조사", Build.MANUFACTURER),
                CrashInfoItem("모델", Build.MODEL),
                CrashInfoItem("Android", Build.VERSION.RELEASE),
                CrashInfoItem("API Level", Build.VERSION.SDK_INT.toString())
            ),
            type = SectionType.CODE
        )
    }

    override fun getOrder(): Int = 50
}
