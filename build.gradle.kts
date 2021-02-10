import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("idea")
}

group = "marregui.minicron"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mockito:mockito-core:3.5.10")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.8"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
