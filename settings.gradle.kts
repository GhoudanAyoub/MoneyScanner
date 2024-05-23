pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io")}
    }
}


rootProject.name = "Chari Money Scanner"
include(":app")
include(":feature")
include(":testing:data")
include(":testing:domain")
include(":feature:data")
