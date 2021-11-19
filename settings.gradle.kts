pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "mvi-core"
includeBuild("convention-plugins")

enableFeaturePreview("VERSION_CATALOGS")
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("gradle/wrapper/libs.versions.toml")) } }
}