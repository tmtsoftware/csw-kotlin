val serializationVersion: String by project
val arrowVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

version = "6.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":csw-params"))
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    //implementation("io.arrow-kt:arrow-fx-coroutines")
    //implementation("io.arrow-kt:arrow-fx-stm")
    implementation("io.arrow-kt:arrow-core-serialization")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$serializationVersion")
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotestVersion}")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}