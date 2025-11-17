# Android-Crash-Report-Activity

[![Hits](https://myhits.vercel.app/api/hit/https%3A%2F%2Fgithub.com%2Fdonglab%2FAndroid-Crash-Report-Activity%3Ftab%3Dreadme-ov-file?color=blue&label=hits&size=small)](https://myhits.vercel.app)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/min%20sdk-21-green?style=flat-square)](https://developer.android.com)
[![Jitpack](https://jitpack.io/v/donglab/Android-Crash-Report-Activity.svg)](https://jitpack.io/#donglab/Android-Crash-Report-Activity)

**[English README](./README.md)**

## 개요

Android-Crash-Report-Activity는 크래시로 인해 앱이 종료될 때 크래시 정보를 UI로 표시하는 디버그 라이브러리입니다. 

앱 정보, 디바이스 정보, 메모리 상태, 네트워크 상태, 스택 트레이스를 포함한 포괄적인 크래시 상세 정보를 제공합니다.


| 샘플 화면 | 크래시 정보 화면 1 | 크래시 정보 화면 2 | 공유하기 |
|---------|----------------|----------------|----------------|
| <img width="1080" height="2640" alt="image" src="https://github.com/user-attachments/assets/af2d878d-09cf-4c95-ae1d-40afdae1b76b" /> | <img width="1080" height="2640" alt="image" src="https://github.com/user-attachments/assets/58b8bd59-b5ea-4079-8ead-6c0297289cab" /> | <img width="1080" alt="image" src="https://github.com/user-attachments/assets/85fc7707-45d3-4d45-8744-abca5cd4982c" /> | <img width="1080" alt="image" src="https://github.com/user-attachments/assets/d7e845d8-c299-4b6c-9521-193b5c8df302" /> |

<br>

이 라이브러리는 개발 및 QA 테스팅 중 재현이 어려운 크래시 문제를 해결합니다. 

크래시 발생 시 상세한 정보를 공유 가능한 UI로 표시하여, QA 담당자와 개발자가 재현 불가능한 상황에서도 즉시 크래시 로그를 캡처하고 공유할 수 있습니다.

<br>

## 주요 기능

- **자동 크래시 감지**: 처리되지 않은 예외를 자동으로 포착하여 크래시 정보 표시
- **포괄적인 정보 수집**: 크래시 시간, 화면 이름, 예외 상세 정보, 앱 정보, 디바이스 정보, 빌드 정보, 스레드 정보, 메모리 상태, 네트워크 상태 수집
- **깔끔한 UI 디자인**: 적절한 스타일링으로 크래시 정보를 체계적이고 읽기 쉬운 형식으로 표시
- **공유 기능**: 사용자가 크래시 리포트를 공유 가능한 앱을 통해 공유 가능
- **커스터마이징 가능한 Provider**: 깔끔한 DSL API를 통해 커스텀 크래시 정보 Provider 지원
- **설정 불필요**: 합리적인 기본값으로 즉시 사용 가능하며, 동시에 높은 커스터마이징 가능

<br>

## 설치

### Step 1: Jitpack 저장소 추가

프로젝트의 `settings.gradle.kts`에 Jitpack 저장소를 추가합니다:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: 의존성 추가

모듈의 `build.gradle.kts`에 라이브러리를 추가합니다:

```kotlin
dependencies {
    implementation("com.github.donglab:Android-Crash-Report-Activity:latest.release")
}
```

<br>

### 요구사항

- Android API 21 (Android 5.0) 이상
- Kotlin 지원

<br>

## 사용법

### Application 클래스에서 초기화

크래시 리포팅을 활성화하려면 Application 클래스에서 `installCrashHandler`를 호출하기만 하면 됩니다:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        installCrashHandler(this) {
            providers {
                useDefault()  // 모든 기본 Provider 사용
            }
        }
    }
}
```



### 커스텀 Provider (고급)

`CrashInfoProvider` 인터페이스를 구현하여 커스텀 크래시 정보 Provider를 추가할 수 있습니다:

```kotlin
class MyCustomProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection? {
        return CrashInfoSection(
            title = "커스텀 정보",
            type = SectionType.NORMAL,
            items = listOf(
                CrashInfoItem(
                    label = "커스텀 필드",
                    value = "커스텀 값",
                    type = ItemType.NORMAL
                )
            )
        )
    }
}

// 커스텀 Provider 추가
installCrashHandler(this) {
    providers {
        useDefault()  // 기본 Provider 사용
        add(MyCustomProvider())  // 커스텀 Provider 추가
    }
}
```


<br>

### 사용 가능한 Provider

라이브러리는 `useDefault()`를 호출할 때 자동으로 사용되는 여러 내장 Provider를 포함합니다:

- **BasicInfoProvider** (필수): 크래시 시간과 화면 이름 수집
- **ExceptionInfoProvider** (필수): 예외 타입, 메시지, 스택 트레이스 수집
- **AppInfoProvider**: 앱 버전, 패키지명, 버전 코드 수집
- **BuildInfoProvider**: 빌드 관련 정보 수집 (SDK 버전, 제조사 등)
- **DeviceInfoProvider**: 디바이스 모델, Android 버전, 하드웨어 정보 수집
- **ThreadInfoProvider**: 크래시가 발생한 스레드 정보 수집
- **MemoryInfoProvider**: 메모리 사용량 정보 수집 (힙 크기, 사용 가능한 메모리 등)
- **NetworkInfoProvider**: 네트워크 연결 상태 수집

`useDefault()`를 사용하여 모든 기본 Provider를 사용하거나, 필요한 Provider만 선택적으로 추가할 수 있습니다.

<br>

## 기여자

<!-- readme: collaborators,contributors -start -->
<table>
	<tbody>
		<tr>
            <td align="center">
                <a href="https://github.com/donglab">
                    <img src="https://avatars.githubusercontent.com/donglab" width="100;" alt="donglab"/>
                    <br />
                    <sub><b>Donglab</b></sub>
                </a>
            </td>
		</tr>
	<tbody>
</table>
<!-- readme: collaborators,contributors -end -->

