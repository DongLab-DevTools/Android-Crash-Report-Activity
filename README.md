# Android-Crash-Report-Activity

[![Hits](https://myhits.vercel.app/api/hit/https%3A%2F%2Fgithub.com%2Fdonglab%2FAndroid-Crash-Report-Activity%3Ftab%3Dreadme-ov-file?color=blue&label=hits&size=small)](https://myhits.vercel.app)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/min%20sdk-21-green?style=flat-square)](https://developer.android.com)
[![Jitpack](https://jitpack.io/v/donglab/Android-Crash-Report-Activity.svg)](https://jitpack.io/#donglab/Android-Crash-Report-Activity)

**[한국어 README](./README_ko.md)**

## Overview

Android-Crash-Report-Activity is a debug library that displays crash information in a UI when the app terminates due to a crash.

It provides comprehensive crash details including app information, device information, memory status, network status, and stack trace.

| Sample Screen | Crash Info Screen 1 | Crash Info Screen 2 | Share Feature |
|---------|----------------|----------------|----------------|
| <img width="1080" height="2640" alt="image" src="https://github.com/user-attachments/assets/af2d878d-09cf-4c95-ae1d-40afdae1b76b" /> | <img width="1080" height="2640" alt="image" src="https://github.com/user-attachments/assets/58b8bd59-b5ea-4079-8ead-6c0297289cab" /> | <img width="1080" alt="image" src="https://github.com/user-attachments/assets/85fc7707-45d3-4d45-8744-abca5cd4982c" /> | <img width="1080" alt="image" src="https://github.com/user-attachments/assets/d7e845d8-c299-4b6c-9521-193b5c8df302" /> |



<br>

This library solves the problem of non-reproducible crashes during development and QA testing. When a crash occurs, it displays detailed crash information in a shareable UI, allowing QA testers and developers to immediately capture and share crash logs even when the issue can't be reproduced.

<br>

## Features

- **Automatic Crash Detection**: Automatically catches uncaught exceptions and displays crash information
- **Comprehensive Information Collection**: Collects crash time, screen name, exception details, app info, device info, build info, thread info, memory status, and network status
- **Clean UI Design**: Displays crash information in a well-organized, readable format with proper styling
- **Share Functionality**: Allows users to share crash reports via any sharing-enabled app
- **Customizable Providers**: Supports custom crash information providers through a clean DSL API
- **Zero Configuration**: Works out of the box with sensible defaults, while still being highly customizable

<br>

## Installation

### Step 1: Add Jitpack repository

Add the Jitpack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add dependency

Add the library to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.donglab:Android-Crash-Report-Activity:latest.release")
}
```

<br>

### Requirements

- Android API 21 (Android 5.0) or higher
- Kotlin support

<br>

## Usage

### Initialize in Application class

To enable crash reporting, simply call `installCrashHandler` in your Application class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        installCrashHandler(this) {
            providers {
                useDefault()  // Use all default providers
            }
        }
    }
}
```


### Custom Provider (Advanced)

You can add custom crash information providers by implementing the `CrashInfoProvider` interface:

```kotlin
class MyCustomProvider : CrashInfoProvider {
    override fun collect(
        context: Context,
        throwable: Throwable,
        thread: Thread,
        activityName: String
    ): CrashInfoSection? {
        return CrashInfoSection(
            title = "Custom Info",
            type = SectionType.NORMAL,
            items = listOf(
                CrashInfoItem(
                    label = "Custom Field",
                    value = "Custom Value",
                    type = ItemType.NORMAL
                )
            )
        )
    }
}

// Add custom provider
installCrashHandler(this) {
    providers {
        useDefault()  // Use default providers
        add(MyCustomProvider())  // Add your custom provider
    }
}
```

<br>

### Available Providers

The library includes several built-in providers that are automatically used when you call `useDefault()`:

- **BasicInfoProvider** (Required): Collects crash time and screen name
- **ExceptionInfoProvider** (Required): Collects exception type, message, and stack trace
- **AppInfoProvider**: Collects app version, package name, and version code
- **BuildInfoProvider**: Collects build-related information (SDK version, manufacturer, etc.)
- **DeviceInfoProvider**: Collects device model, Android version, and hardware info
- **ThreadInfoProvider**: Collects information about the thread where the crash occurred
- **MemoryInfoProvider**: Collects memory usage information (heap size, available memory, etc.)
- **NetworkInfoProvider**: Collects network connection status

You can use all default providers with `useDefault()`, or selectively add only the providers you need.

<br>

## Contributors

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
