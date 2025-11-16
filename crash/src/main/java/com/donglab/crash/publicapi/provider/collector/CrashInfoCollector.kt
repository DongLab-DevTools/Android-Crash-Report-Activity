package com.donglab.crash.publicapi.provider.collector

import android.content.Context
import com.donglab.crash.publicapi.provider.CrashInfoProvider
import com.donglab.crash.publicapi.provider.impl.AppInfoProvider
import com.donglab.crash.publicapi.provider.impl.BasicInfoProvider
import com.donglab.crash.publicapi.provider.impl.BuildInfoProvider
import com.donglab.crash.publicapi.provider.impl.DeviceInfoProvider
import com.donglab.crash.publicapi.provider.impl.ExceptionInfoProvider
import com.donglab.crash.publicapi.provider.impl.MemoryInfoProvider
import com.donglab.crash.publicapi.provider.impl.NetworkInfoProvider
import com.donglab.crash.publicapi.provider.impl.ThreadInfoProvider
import com.donglab.crash.publicapi.provider.model.CrashInfoSection

/**
 * CrashInfoProvider 관리자 (내부 사용)
 * Provider들을 등록하고 크래시 정보를 수집합니다.
 *
 * 외부에서는 DSL API(installCrashHandler)를 사용하세요.
 *
 * **중복 방지**: Provider는 타입(클래스)을 기준으로 관리되며,
 * 같은 타입의 Provider를 여러 번 등록하면 마지막 것으로 대체됩니다.
 */
internal class CrashInfoCollector {

    // Provider 타입(클래스)을 키로 사용하여 중복 방지 및 빠른 조회
    private val providers = mutableMapOf<Class<out CrashInfoProvider>, CrashInfoProvider>()

    /**
     * Provider 등록
     * 같은 타입의 Provider는 중복 등록되지 않습니다 (마지막에 등록된 것으로 대체)
     */
    fun register(provider: CrashInfoProvider) {
        providers[provider.javaClass] = provider
    }

    /**
     * 여러 Provider를 한 번에 등록
     */
    fun registerAll(vararg newProviders: CrashInfoProvider) {
        newProviders.forEach { register(it) }
    }

    /**
     * Provider 등록 해제 (타입으로 제거)
     */
    fun unregister(provider: CrashInfoProvider) {
        providers.remove(provider.javaClass)
    }

    /**
     * 특정 타입의 Provider 등록 해제
     */
    inline fun <reified T : CrashInfoProvider> unregisterByType() {
        providers.remove(T::class.java)
    }

    /**
     * 모든 Provider 제거
     */
    fun clear() {
        providers.clear()
    }

    /**
     * 필수 Provider들을 등록 (항상 포함됨)
     */
    fun registerRequiredProviders() {
        registerAll(
            BasicInfoProvider(),      // 발생 시간, 화면
            ExceptionInfoProvider()   // 예외 정보
        )
    }

    /**
     * 기본 Provider들을 등록
     */
    fun registerDefaultProviders() {
        registerAll(
            BuildInfoProvider(),
            ThreadInfoProvider(),
            MemoryInfoProvider(),
            NetworkInfoProvider(),
            DeviceInfoProvider(),
            AppInfoProvider()
        )
    }

    /**
     * 등록된 모든 Provider로부터 크래시 정보 수집
     * Order 값에 따라 정렬하여 수집합니다
     */
    fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): List<CrashInfoSection> {
        val sections = mutableListOf<CrashInfoSection>()

        // Order 값으로 정렬하여 수집
        providers.values
            .sortedBy { it.getOrder() }
            .forEach { provider ->
                try {
                    provider.collect(context, throwable, thread, activityName)?.let {
                        sections.add(it)
                    }
                } catch (e: Exception) {
                    // Provider에서 에러가 발생해도 다른 Provider는 계속 수집
                    e.printStackTrace()
                }
            }

        return sections
    }

    /**
     * 현재 등록된 Provider 개수
     */
    fun getProviderCount(): Int = providers.size

    /**
     * 특정 타입의 Provider가 등록되어 있는지 확인
     */
    inline fun <reified T : CrashInfoProvider> hasProvider(): Boolean {
        return providers.values.any { it is T }
    }
}
