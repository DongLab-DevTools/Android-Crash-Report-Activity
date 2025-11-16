package com.donglab.crash.provider.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 크래시 정보 아이템 (라벨-값 쌍)
 */
@Parcelize
data class CrashInfoItem(
    val label: String,
    val value: String,
    val type: ItemType = ItemType.NORMAL
) : Parcelable
