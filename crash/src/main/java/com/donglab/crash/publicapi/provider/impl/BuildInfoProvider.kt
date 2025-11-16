package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.SectionType

/**
 * 빌드 정보 Provider (새로 추가)
 * - Build Type (debug/release)
 * - Product Flavor
 *
 * Note: BuildConfig는 core:crash 모듈의 것이 아니라
 * 앱 모듈의 BuildConfig를 리플렉션으로 가져옵니다.
 */
class BuildInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection? {
        return try {
            // 앱의 BuildConfig를 리플렉션으로 가져오기
            val packageName = context.packageName
            val buildConfigClass = Class.forName("$packageName.BuildConfig")

            val buildType = try {
                buildConfigClass.getField("BUILD_TYPE").get(null) as? String ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }

            val flavor = try {
                buildConfigClass.getField("FLAVOR").get(null) as? String ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }

            val items = mutableListOf<CrashInfoItem>()

            if (buildType != "Unknown") {
                items.add(CrashInfoItem("Build Type", buildType))
            }

            if (flavor != "Unknown" && flavor.isNotEmpty()) {
                items.add(CrashInfoItem("Flavor", flavor))
            }

            if (items.isNotEmpty()) {
                CrashInfoSection(
                    title = "빌드 정보",
                    items = items,
                    type = SectionType.CODE
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // BuildConfig를 찾을 수 없으면 null 반환
            null
        }
    }
}
