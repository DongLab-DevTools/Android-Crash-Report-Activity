package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.SectionType

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

            val (isConnected, networkType) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
                val connected = capabilities != null
                val type = when {
                    capabilities == null -> "None"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
                    else -> "Unknown"
                }
                Pair(connected, type)
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = cm?.activeNetworkInfo
                val connected = networkInfo?.isConnected ?: false
                val type = networkInfo?.typeName ?: "None"
                Pair(connected, type)
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
                    CrashInfoItem("Type", "Error: ${e.message}")
                ),
                type = SectionType.CODE
            )
        }
    }

    override fun getOrder(): Int = 40
}
