package com.donglab.crash.provider.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 섹션 타입
 */
@Parcelize
enum class SectionType : Parcelable {
    NORMAL,     // 일반 섹션
    EXCEPTION,  // 예외 정보 (강조 표시)
    CODE        // 코드 (monospace 폰트)
}

/**
 * 아이템 타입
 */
@Parcelize
enum class ItemType : Parcelable {
    NORMAL,     // 일반 텍스트
    CODE,       // 코드 (monospace 폰트)
    ERROR       // 에러 (빨간색 강조)
}
