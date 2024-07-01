plugins {
    java
    application
    base
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.55.0"
    id("lifecycle-base")
}

group = "it.unibo.ares"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.6")
    implementation("org.ini4j:ini4j:0.5.4")
    

    val jUnitVersion = "5.10.2"
    // JUnit API and testing engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    testImplementation("org.mockito:mockito-core:5.12.0")
    // testImplementation group: 'org.mockito', name: 'mockito-core', version: '5.12.0'
}

tasks.test {
    useJUnitPlatform()
}

application {
    // Define the main class for the application
    mainClass.set("it.unibo.ares.core.App")
}
