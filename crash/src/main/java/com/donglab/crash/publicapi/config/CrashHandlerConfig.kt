package com.donglab.crash.publicapi.config

import com.donglab.crash.publicapi.provider.CrashInfoProvider

/**
 * CrashHandler 설정
 */
class CrashHandlerConfig {
    internal val providersConfig = ProvidersConfig()

    /**
     * Provider 설정
     */
    fun providers(block: ProvidersConfig.() -> Unit) {
        providersConfig.apply(block)
    }
}

/**
 * Provider 설정
 */
class ProvidersConfig {
    internal val providers = mutableListOf<CrashInfoProvider>()
    internal var useDefaultProviders = false

    /**
     * 기본 Provider들 사용
     */
    fun useDefault() {
        useDefaultProviders = true
    }

    /**
     * Provider 추가
     */
    fun add(provider: CrashInfoProvider) {
        providers.add(provider)
    }

    /**
     * 여러 Provider 한 번에 추가
     */
    fun addAll(vararg newProviders: CrashInfoProvider) {
        providers.addAll(newProviders)
    }
}
