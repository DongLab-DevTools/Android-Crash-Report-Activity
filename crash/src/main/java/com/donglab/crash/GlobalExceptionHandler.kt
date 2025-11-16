package com.donglab.crash

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import com.donglab.crash.config.CrashHandlerConfig
import com.donglab.crash.provider.collector.CrashInfoCollector
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val application: Application,
    config: CrashHandlerConfig
) : Thread.UncaughtExceptionHandler {

    private val crashlyticsExceptionHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    private var lastActivityRef: WeakReference<Activity>? = null

    private val crashInfoCollector = CrashInfoCollector()

    init {
        // 필수 Provider는 항상 등록 (크래시 정보, 예외 정보)
        crashInfoCollector.registerRequiredProviders()

        // Provider 설정
        if (config.providersConfig.useDefaultProviders) {
            crashInfoCollector.registerDefaultProviders()
        }
        config.providersConfig.providers.forEach { provider ->
            crashInfoCollector.register(provider)
        }

        // ActivityLifecycleCallbacks로 마지막 Activity 추적
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity !is CrashActivity) {
                        lastActivityRef = WeakReference(activity)
                    }
                }

                override fun onActivityStarted(activity: Activity) {
                    if (activity !is CrashActivity) {
                        lastActivityRef = WeakReference(activity)
                    }
                }

                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

                override fun onActivityDestroyed(activity: Activity) {
                    // 메모리 누수 방지: destroy된 activity가 lastActivity면 clear
                    if (lastActivityRef?.get() == activity) {
                        lastActivityRef?.clear()
                        lastActivityRef = null
                    }
                }
            })

        // 이 Handler를 기본 Handler로 설정
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // 마지막 Activity가 있으면 CrashActivity 시작
            lastActivityRef?.get()?.run {
                startCrashActivity(this, thread, throwable)
            }

            // Crashlytics에 보고 (Firebase가 알아서 처리)
            crashlyticsExceptionHandler?.uncaughtException(thread, throwable)

            // 프로세스 종료
            Process.killProcess(Process.myPid())
            exitProcess(-1)

        } catch (e: Exception) {
            // 실패하면 기본 handler에게 맡김
            crashlyticsExceptionHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun startCrashActivity(activity: Activity, thread: Thread, throwable: Throwable) {
        try {
            // CrashInfo 생성 (Provider 패턴으로 정보 수집)
            val sections = crashInfoCollector.collect(
                context = application,
                throwable = throwable,
                thread = thread,
                activityName = activity.javaClass.simpleName
            )

            val crashInfo = CrashInfo(sections = sections)

            // CrashActivity 시작
            val intent = Intent(application, CrashActivity::class.java).apply {
                putExtra(CrashActivity.EXTRA_CRASH_INFO, crashInfo)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

            application.startActivity(intent)
            activity.finish()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start CrashActivity", e)
        }
    }

    companion object {
        private const val TAG = "GlobalExceptionHandler"

        @Volatile
        private var instance: GlobalExceptionHandler? = null

        internal fun install(application: Application, config: CrashHandlerConfig): GlobalExceptionHandler {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GlobalExceptionHandler(application, config)
                    }
                }
            }
            return instance!!
        }
    }
}
