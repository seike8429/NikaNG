pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // FIX 2026-11-08 (run #5): Maven Central returned 504 Gateway
        // Time-out downloading kotlin-compiler-embeddable-2.1.0.jar.
        // Try Google's Maven Central mirror first; mavenCentral() stays
        // as fallback.
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // FIX 2026-11-08 (run #5): see pluginManagement above — Google's
        // Maven Central mirror first, mavenCentral() as fallback.
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "SpiderProxyFinder"
include(":app")
include(":rxpermissions")
