import java.io.*
import java.util.*
/*
// read gradle.properties programmatically
val props = Properties()
FileInputStream(file("../gradle.properties")).use {
    props.load(it)
}

*/

plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}

/*

kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
    jvmToolchain(21)
}

 */

/*

dependencies {
    val kotlinVersion = props.getProperty("kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
}