Android Studio java program called CaesarCipher (uses google native UI instead of JavaFX); slide Shift 13 Encrypt/Decrypt like ROT13

SNIPPET - build.gradle:

alias(libs.plugins.android.application) apply false

SNIPPET - gradle.properties:

org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

android.useAndroidX=true

android.nonTransitiveRClass=true

SNIPPET - settings.gradle:

            includeGroupByRegex("com\\.android.*")

            includeGroupByRegex("com\\.google.*")

            includeGroupByRegex("androidx.*")
    
    mavenCentral()

    gradlePluginPortal()

id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'

repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    google()

    mavenCentral()

rootProject.name = "CaesarCipher"

(><)
