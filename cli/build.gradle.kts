plugins {
    id("java")

    application

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.29.0"

}

group = "it.unibo.ares"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")

    val jUnitVersion = "5.10.1"
    // JUnit API and testing engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    implementation(project(":core"))

}
application {
    // Define the main class for the application
    mainClass.set("it.unibo.ares.cli.App")
}
