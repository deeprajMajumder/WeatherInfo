
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
buildscript{
    repositories {
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }
    dependencies {
        // Add the dependency for the Google services Gradle plugin
        classpath ("com.google.gms:google-services:4.4.1")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.0")
    }
}