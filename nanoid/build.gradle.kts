plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("maven-publish")
}
group = "de.datlag"
version = Versions.releaseVersion

android {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion(Versions.buildToolsVersion)

    defaultConfig {
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode(Versions.releaseVersionCode)
        versionName(Versions.releaseVersion)
    }

    buildTypes {
        val debug by getting {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
        }

        val release by getting {
            isMinifyEnabled = false
            isDebuggable = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

repositories {
    addRepos()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.java.toString()
        }
    }
    js {
        browser()
        nodejs()
    }
    android {
        publishAllLibraryVariants()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.soywiz.korlibs.krypto:krypto:${Versions.krypto}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
        }
        val androidTest by getting {
            dependsOn(jvmTest)
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting { }
        val nativeTest by getting { }
    }
}

publishing {
    publications.withType<MavenPublication>().apply {
        val jvm by getting { }
        val js by getting { }
        val metadata by getting { }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/DatL4g/NanoId")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}