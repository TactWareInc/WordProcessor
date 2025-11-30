import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id("com.vanniktech.maven.publish") version "0.35.0"
    signing
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.core)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "net.tactware.wordprocessor"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    // Configure publishing to Maven Central
    publishToMavenCentral()

    signAllPublications()

    // Configure project coordinates
    coordinates(rootProject.group as String, "core", rootProject.version as String)

    // Configure POM metadata
    pom {
        name.set("WordProcessor Core")
        description.set("Core word processing utilities for Kotlin Multiplatform")
        inceptionYear.set("2024")
        url.set("https://github.com/TactWareInc/WordProcessor")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("kmbisset89")
                name.set("Kerry Bisset")
                url.set("https://github.com/kmbisset89")
            }
        }

        scm {
            url.set("https://github.com/TactWareInc/WordProcessor")
            connection.set("scm:git:git://github.com/TactWareInc/WordProcessor.git")
            developerConnection.set("scm:git:ssh://git@github.com/TactWareInc/WordProcessor.git")
        }
    }
}

signing {
    sign(publishing.publications)
    useInMemoryPgpKeys(
        findProperty("signingKey") as String?,
        findProperty("signing.password") as String?
    )
}


tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}