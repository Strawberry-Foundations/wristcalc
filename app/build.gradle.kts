plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.strawberryfoundations.wear.calculator"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "org.strawberryfoundations.wear.calculator"
        minSdk = 33
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    // General compose dependencies
    implementation(composeBom)
    implementation(libs.compose.activity)
    implementation(libs.splashscreen)

    // Material icons
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)

    // Data store
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)

    // M3 & Play Services
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.material3)
    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.navigation)
    implementation(libs.wear.gms.playservices)

    // Horologist for correct Compose layout
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)

    // JSON
    implementation(libs.kotlinx.serialization.json)

    // Preview Tooling
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling)

    implementation(libs.compose.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    testImplementation(libs.horologist.roboscreenshots)

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(composeBom)
}