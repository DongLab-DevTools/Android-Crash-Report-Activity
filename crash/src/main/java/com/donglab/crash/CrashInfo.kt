package com.donglab.crash

import android.os.Parcelable
import com.donglab.crash.provider.model.CrashInfoSection
import kotlinx.parcelize.Parcelize

/**
 * 크래시 정보를 담는 데이터 클래스
 * Provider 패턴을 통해 수집된 섹션들을 포함합니다.
 */
@Parcelize
data class CrashInfo(
    val sections: List<CrashInfoSection>
) : Parcelable {

    /**
     * 공유용 텍스트 포맷
     */
    fun getFormattedText(): String {
        return buildString {
            appendLine("=== CRASH REPORT ===")
            appendLine()

            sections.forEach { section ->
                appendLine("=== ${section.title.uppercase()} ===")
                section.items.forEach { item ->
                    if (item.label.isNotEmpty()) {
                        appendLine("${item.label}: ${item.value}")
                    } else {
                        appendLine(item.value)
                    }
                }
                appendLine()
            }
        }
    }
}
