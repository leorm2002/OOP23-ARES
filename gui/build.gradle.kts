plugins {
    java
    application

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.55.0"
    id("lifecycle-base")
}

group = "it.unibo.ares"
version = "1.0.0"

repositories {
    mavenCentral()
}
val javaFXModules = listOf(
        "base",
        "controls",
        "fxml",
        "swing",
        "graphics"
)

val supportedPlatforms = listOf("linux", "mac", "win", "mac-aarch64") // All required for OOP

dependencies {
    val javaFxVersion = "22.0.1"
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.6")

    val jUnitVersion = "5.10.2"
    // JUnit API and testing engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("it.unibo.ares.gui.App")
}