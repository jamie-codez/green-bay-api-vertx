import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "1.8.20"
    application
    id("io.vertx.vertx-plugin") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

fun getVersionName():Any{
    return try {
        val stdOut = ByteArrayOutputStream()
        exec{
            commandLine = listOf("git","describe","--tags")
            standardOutput = stdOut
        }
        val delim = "-"
        val list = stdOut.toString().split(delim)
        list[0].replace("\n","")
    }catch (e:Exception){
        ""
    }
}

group = "com.greenbay.api"
version = getVersionName()

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-core:4.3.7"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-mongo-client")
    implementation("io.vertx:vertx-mail-client")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-health-check")
    implementation("com.auth0:java-jwt:4.2.2")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.springframework.security:spring-security-crypto:5.5.4")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("org.json:json:20230227")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testImplementation("io.vertx:vertx-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
}

val mainVerticleName = "com.greenbay.api.GreenBayService"
val watchForChange = "src/**/*"
val doChange = "$projectDir/gradlew classes"

vertx{
    mainVerticle = mainVerticleName
}

application{
    val mainClassName = "io.vertx.core.Launcher"
    mainClass.set(mainClassName)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}