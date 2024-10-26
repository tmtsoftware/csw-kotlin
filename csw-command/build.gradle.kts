val kotlinx_serialization_version: String by project
val kotest_version: String by project
val kotlinx_date_time_version: String by project
val arrow_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

//group = "csw"
//version = "6.0"

repositories {
    mavenCentral()
}

dependencies {
//    implementation(platform("io.arrow-kt:arrow-stack:$arrow_version"))
//    implementation("io.arrow-kt:arrow-core")
//    implementation("io.arrow-kt:arrow-optics")
    //implementation("io.arrow-kt:arrow-fx-coroutines")
    //implementation("io.arrow-kt:arrow-fx-stm")
//    implementation("io.arrow-kt:arrow-core-serialization")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$kotlinx_serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_date_time_version")
    implementation("net.java.dev.jna:jna:5.14.0")

    //testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-json:$kotest_version")
  //  testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}