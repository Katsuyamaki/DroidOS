plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.katsuyamaki.DroidOSLauncher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSLauncher"
        minSdk = 29
        targetSdk = 35
        versionCode = 4
        versionName = "2.2"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val githubAuthExchangeUrl = ((project.findProperty("GITHUB_AUTH_EXCHANGE_URL") as String?) ?: "")
        .trim()
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
    val githubAllowManualTokenImport = ((project.findProperty("GITHUB_ALLOW_MANUAL_TOKEN_IMPORT") as String?) ?: "true")
        .trim()
        .equals("true", ignoreCase = true)

    flavorDimensions += "store"
    productFlavors {
        create("internal") {
            dimension = "store"
            buildConfigField("String", "STORE_CHANNEL", "\"internal\"")
            buildConfigField("boolean", "USE_PLAY_BILLING", "true")
            buildConfigField("boolean", "USE_GITHUB_AUTH", "true")
            buildConfigField("String", "AUTH_PORTAL_URL", "\"https://katsuyamaki.github.io/\"")
            buildConfigField("String", "AUTH_EXCHANGE_URL", "\"$githubAuthExchangeUrl\"")
            buildConfigField("boolean", "ALLOW_GITHUB_MANUAL_TOKEN_IMPORT", "$githubAllowManualTokenImport")
            buildConfigField("boolean", "IS_INTERNAL_TESTING_BINARY", "true")
        }
        create("play") {
            dimension = "store"
            buildConfigField("String", "STORE_CHANNEL", "\"play\"")
            buildConfigField("boolean", "USE_PLAY_BILLING", "true")
            buildConfigField("boolean", "USE_GITHUB_AUTH", "false")
            buildConfigField("String", "AUTH_PORTAL_URL", "\"\"")
            buildConfigField("String", "AUTH_EXCHANGE_URL", "\"\"")
            buildConfigField("boolean", "ALLOW_GITHUB_MANUAL_TOKEN_IMPORT", "false")
            buildConfigField("boolean", "IS_INTERNAL_TESTING_BINARY", "false")
        }
        create("samsung") {
            dimension = "store"
            buildConfigField("String", "STORE_CHANNEL", "\"samsung\"")
            buildConfigField("boolean", "USE_PLAY_BILLING", "false")
            buildConfigField("boolean", "USE_GITHUB_AUTH", "false")
            buildConfigField("String", "AUTH_PORTAL_URL", "\"\"")
            buildConfigField("String", "AUTH_EXCHANGE_URL", "\"\"")
            buildConfigField("boolean", "ALLOW_GITHUB_MANUAL_TOKEN_IMPORT", "false")
            buildConfigField("boolean", "IS_INTERNAL_TESTING_BINARY", "false")
        }
        create("github") {
            dimension = "store"
            buildConfigField("String", "STORE_CHANNEL", "\"github\"")
            buildConfigField("boolean", "USE_PLAY_BILLING", "false")
            buildConfigField("boolean", "USE_GITHUB_AUTH", "true")
            buildConfigField("String", "AUTH_PORTAL_URL", "\"https://katsuyamaki.github.io/\"")
            buildConfigField("String", "AUTH_EXCHANGE_URL", "\"$githubAuthExchangeUrl\"")
            buildConfigField("boolean", "ALLOW_GITHUB_MANUAL_TOKEN_IMPORT", "$githubAllowManualTokenImport")
            buildConfigField("boolean", "IS_INTERNAL_TESTING_BINARY", "false")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
    }

    sourceSets {
        getByName("main") {
            aidl.srcDirs(listOf("src/main/aidl"))
            java.srcDirs(layout.buildDirectory.dir("generated/source/aidl/debug"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
    implementation("dev.rikka.shizuku:aidl:13.1.5")

}
