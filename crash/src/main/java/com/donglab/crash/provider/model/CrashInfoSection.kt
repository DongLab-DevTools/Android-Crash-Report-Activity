package com.donglab.crash.provider.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 크래시 정보 섹션 (화면의 한 그룹)
 */
@Parcelize
data class CrashInfoSection(
    val title: String,
    val items: List<CrashInfoItem>,
    val type: SectionType = SectionType.NORMAL
) : Parcelable
