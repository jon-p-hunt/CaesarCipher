Android Studio java program called CaesarCipher (uses google native UI instead of JavaFX); slide Shift 13 Encrypt/Decrypt like ROT13

SNIPPET - build.gradle:

    id 'com.android.application' version '8.3.2' apply false



SNIPPET - gradle.properties:

org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

android.useAndroidX=true

android.enableJetifier=true



SNIPPET - settings.gradle:

        google()
 
        mavenCentral()
 
        gradlePluginPortal()
  
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

        google()
 
        mavenCentral()
  
rootProject.name = "CaesarCipher"

include ':app'



(><)
