@file:Suppress("DSL_SCOPE_VIOLATION")

import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    jacoco
}

android {
    namespace = "com.projectgarage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.projectgarage"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Project Garage (Dev)")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "Project Garage")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    lint {
        abortOnError = true
        warningsAsErrors = true
        checkDependencies = true
        htmlReport = true
        xmlReport = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}

jacoco {
    toolVersion = "0.8.14"
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    basePath = rootDir.absolutePath
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

val coverageExclusions =
    listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
    )

fun registerJacocoReportTask(
    name: String,
    variant: String,
    testTaskName: String,
) {
    tasks.register<JacocoReport>(name) {
        dependsOn(testTaskName)

        val kotlinClasses =
            fileTree("$buildDir/tmp/kotlin-classes/$variant") {
                exclude(coverageExclusions)
            }
        val javaClasses =
            fileTree("$buildDir/intermediates/javac/$variant/classes") {
                exclude(coverageExclusions)
            }

        classDirectories.setFrom(files(kotlinClasses, javaClasses))
        sourceDirectories.setFrom(
            files(
                "src/main/java",
                "src/main/kotlin",
                "src/dev/java",
                "src/dev/kotlin",
                "src/prod/java",
                "src/prod/kotlin",
                "src/debug/java",
                "src/debug/kotlin",
                "src/release/java",
                "src/release/kotlin",
                "src/$variant/java",
                "src/$variant/kotlin",
            ),
        )
        executionData.setFrom(
            fileTree(buildDir) {
                include("**/*.exec", "**/*.ec")
            },
        )

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

registerJacocoReportTask(
    name = "jacocoDevDebugReport",
    variant = "devDebug",
    testTaskName = "testDevDebugUnitTest",
)

registerJacocoReportTask(
    name = "jacocoProdReleaseReport",
    variant = "prodRelease",
    testTaskName = "testProdReleaseUnitTest",
)

tasks.named("check") {
    dependsOn("detekt")
}
