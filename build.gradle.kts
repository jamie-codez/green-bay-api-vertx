import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.21"
//    id("org.apache.maven.plugins:maven-surefire-plugin") version "3.0.0-M5"
    application
}

group = "com.code.greenbay.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.3.7")
    implementation("io.vertx:vertx-web:4.3.7")
    implementation("com.auth0:java-jwt:4.2.1")
    implementation("io.vertx:vertx-mail-client:4.3.7")
    implementation("io.vertx:vertx-web-client:4.3.7")
    implementation(dependencyNotation = "io.vertx:vertx-mongo-client:4.3.7")
    testImplementation("io.vertx:vertx-junit5:4.3.7")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}