pluginManagement {
    repositories {
        maven("https://redirector.gvt1.com/edgedl/android/maven2/")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://redirector.gvt1.com/edgedl/android/maven2/")
        mavenCentral()
    }
}

rootProject.name = "ZarArb"
include(":app")
