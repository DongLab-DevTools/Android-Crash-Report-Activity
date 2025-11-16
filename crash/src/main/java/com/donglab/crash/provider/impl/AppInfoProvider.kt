package com.donglab.crash.provider.impl

import android.content.Context
import android.os.Build
import com.donglab.crash.provider.CrashInfoProvider
import com.donglab.crash.provider.model.CrashInfoItem
import com.donglab.crash.provider.model.CrashInfoSection
import com.donglab.crash.provider.model.SectionType

/**
 * 앱 정보 Provider
 * - 패키지명
 * - 버전 이름
 * - 버전 코드
 */
class AppInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }

            val versionName = packageInfo.versionName ?: "Unknown"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            CrashInfoSection(
                title = "앱 정보",
                items = listOf(
                    CrashInfoItem("Package", context.packageName),
                    CrashInfoItem("Version Name", versionName),
                    CrashInfoItem("Version Code", versionCode.toString())
                ),
                type = SectionType.CODE
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun getOrder(): Int = 60
}
