plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)

    id("maven-publish")
}

android {
    namespace = "com.donglab.crash"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        resourcePrefix = "cra_"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.android.material)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.donglab.devtools"
                artifactId = "crash-report-activity"
                version = libs.versions.sdk.version.get()

                // POM Metadata (Optional)
                pom {
                    name.set("Android Crash Report Activity")
                    description.set("An easy-to-use Crash Report Activity for Android applications.")
                    url.set("https://github.com/DongLab-DevTools/Android-Crash-Report-Activity")
                }
            }
        }
    }
}