@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.owasp)
}

buildscript {
    configurations.named("classpath") {
        resolutionStrategy {
            force(
                "org.bouncycastle:bcprov-jdk18on:1.77",
                "org.bouncycastle:bcpkix-jdk18on:1.77",
                "org.bouncycastle:bcutil-jdk18on:1.77",
            )
        }
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
    }
}

dependencyCheck {
    failBuildOnCVSS = 11.0F
    failOnError = false
    formats = listOf("HTML", "XML")
}
