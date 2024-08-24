plugins {
    base
    kotlin("jvm") version "2.0.20" apply false
}

allprojects {

    version = "6.0"

    repositories {
        mavenCentral()
    }
}

dependencies {
    // Make the root project archives configuration depend on every sub-project
    /*
    subprojects.forEach {
        archives(it)
    }

     */
}