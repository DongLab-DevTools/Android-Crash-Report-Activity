package com.donglab.crash.publicapi.provider.impl

import android.content.Context
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoItem
import com.donglab.crash.publicapi.provider.model.CrashInfoSection
import com.donglab.crash.publicapi.provider.model.ItemType
import com.donglab.crash.publicapi.provider.model.SectionType

/**
 * 예외 정보 Provider
 * - 예외 타입
 * - 예외 메시지
 * - 스택 트레이스
 * - Caused by 체인
 */
class ExceptionInfoProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection {
        val items = mutableListOf<CrashInfoItem>()

        // 예외 타입
        items.add(CrashInfoItem(
            label = "예외 타입",
            value = throwable.javaClass.name,
            type = ItemType.ERROR
        ))

        // 예외 메시지
        items.add(CrashInfoItem(
            label = "메시지",
            value = throwable.message ?: "No message"
        ))

        // Caused by 체인
        var cause = throwable.cause
        var depth = 1
        while (cause != null && depth <= 5) {
            items.add(CrashInfoItem(
                label = "Caused by #$depth",
                value = "${cause.javaClass.simpleName}: ${cause.message ?: "No message"}",
                type = ItemType.ERROR
            ))
            cause = cause.cause
            depth++
        }

        // 스택 트레이스
        items.add(CrashInfoItem(
            label = "스택 트레이스",
            value = throwable.stackTraceToString(),
            type = ItemType.CODE
        ))

        return CrashInfoSection(
            title = "예외 정보",
            items = items,
            type = SectionType.EXCEPTION
        )
    }
}
