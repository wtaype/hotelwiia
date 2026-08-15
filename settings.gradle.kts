pluginManagement {
    includeBuild("build-logic")
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
    }
}
rootProject.name = "HotelWii"

include(":app")
include(":core:wii")
include(":core:data")
include(":feature:hola")
include(":feature:auth")
include(":feature:cuenta")
include(":feature:empresas")
include(":feature:recepcion")
include(":feature:actualizar")
include(":feature:imprimir")
