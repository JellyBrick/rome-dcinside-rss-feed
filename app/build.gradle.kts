import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
}

group = "be.zvz"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.12.4")

    implementation(group = "be.zvz", name = "KotlinInside", version = "1.10.2")
    implementation(group = "com.rometools", name = "rome", version = "1.16.0")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
