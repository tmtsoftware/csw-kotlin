val kotest_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.20"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("net.java.dev.jna:jna:5.14.0")

    //testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
}


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

/*
kotlin {
    jvmToolchain(21)
}

 */