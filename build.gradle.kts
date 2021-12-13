@Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("convention.publication")
}

group = "io.github.voitenkodev"
version = "1.0.5"

repositories { mavenCentral() }

kotlin {

    explicitApi()

    jvm {
        compilations.all { kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString() }
        withJava()
        testRuns["test"].executionTask.configure { useJUnitPlatform() }
    }

    js(BOTH) { browser { commonWebpackConfig { cssSupport.enabled = true } } }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting { dependencies { implementation(libs.kotlinx.coroutines) } }
        val commonTest by getting
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}