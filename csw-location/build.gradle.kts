val kotlinx_serialization_version: String by project
val kotest_version: String by project
val arrow_version: String by project
val ktor_version: String by project
val logback_version: String by project


plugins {
    application
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

application {
    mainClass.set("csw.LocationClientApp")
}

version = "6.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":csw-params"))
    implementation(platform("io.arrow-kt:arrow-stack:$arrow_version"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    //implementation("io.arrow-kt:arrow-fx-coroutines")
    //implementation("io.arrow-kt:arrow-fx-stm")
    implementation("io.arrow-kt:arrow-core-serialization")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-cbor:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-json:$kotest_version")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}