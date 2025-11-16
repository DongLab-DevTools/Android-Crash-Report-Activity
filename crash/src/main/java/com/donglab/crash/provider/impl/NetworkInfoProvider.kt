package com.donglab.crash.provider.impl

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.donglab.crash.provider.CrashInfoProvider
import com.donglab.crash.provider.model.CrashInfoItem
import com.donglab.crash.provider.model.CrashInfoSection
import com.donglab.crash.provider.model.SectionType

/**
 * 네트워크 정보 Provider
 * - 연결 상태
 * - 네트워크 타입
 */
class NetworkInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection? {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
            } else {
                @Suppress("DEPRECATION")
                cm?.activeNetworkInfo
            }

            val isConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activeNetwork != null
            } else {
                @Suppress("DEPRECATION")
                (activeNetwork as? android.net.NetworkInfo)?.isConnected ?: false
            }

            val networkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    activeNetwork == null -> "None"
                    else -> "Connected"
                }
            } else {
                @Suppress("DEPRECATION")
                (activeNetwork as? android.net.NetworkInfo)?.typeName ?: "Unknown"
            }

            CrashInfoSection(
                title = "네트워크 정보",
                items = listOf(
                    CrashInfoItem("Connected", isConnected.toString()),
                    CrashInfoItem("Type", networkType)
                ),
                type = SectionType.CODE
            )
        } catch (e: Exception) {
            CrashInfoSection(
                title = "네트워크 정보",
                items = listOf(
                    CrashInfoItem("Connected", "false"),
                    CrashInfoItem("Type", "Error")
                ),
                type = SectionType.CODE
            )
        }
    }

    override fun getOrder(): Int = 40
}
