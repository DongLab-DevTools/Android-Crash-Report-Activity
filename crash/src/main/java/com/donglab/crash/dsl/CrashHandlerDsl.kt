package com.donglab.crash.dsl

import android.app.Application
import com.donglab.crash.GlobalExceptionHandler
import com.donglab.crash.config.CrashHandlerConfig

/**
 * CrashHandler DSL 진입점
 *
 * 사용 예시:
 * ```kotlin
 * installCrashHandler(this) {
 *     providers {
 *         useDefault()  // 기본 Provider 모두 사용
 *         add(MyCustomProvider())  // 커스텀 추가
 *     }
 *
 *     ui {
 *         appBarTitle = "앱 오류"
 *         shareButtonText = "공유"
 *         closeButtonText = "확인"
 *     }
 * }
 * ```
 */
fun installCrashHandler(
    application: Application,
    config: CrashHandlerConfig.() -> Unit = {}
): GlobalExceptionHandler {
    val configuration = CrashHandlerConfig().apply(config)

    return GlobalExceptionHandler.Companion.install(application, configuration)
}
