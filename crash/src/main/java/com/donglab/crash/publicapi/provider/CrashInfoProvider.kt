package com.donglab.crash.publicapi.provider

import android.content.Context
import com.donglab.crash.publicapi.provider.model.CrashInfoSection

/**
 * 크래시 정보 수집 Provider 인터페이스
 * 각 정보 수집기는 이 인터페이스를 구현하여 독립적으로 동작
 */
interface CrashInfoProvider {
    /**
     * 크래시 정보를 수집하여 섹션으로 반환
     * @param context Application Context
     * @param throwable 발생한 예외
     * @param thread 예외가 발생한 스레드
     * @param activityName 예외가 발생한 Activity 이름
     * @return 수집된 정보 섹션
     */
    fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection?

    /**
     * 화면에 표시될 순서 (낮을수록 먼저 표시)
     */
    fun getOrder(): Int
}