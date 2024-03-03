plugins {
    id("java")
}

group = "it.unibo.ares"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":cli"))
    implementation(project(":gui"))
    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}