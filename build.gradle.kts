import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    bootJar {
        archiveFileName.set("demo.jar")
    }

    processResources {
        expand(project.properties)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    disableCodeFormattingChecks("ktlintFormat", "ktlintCheck")
}

springBoot { mainClass.set("com.example.RetweetCounterApplicationKt") }

/**
 * Disable code formatting checks,
 * when e.x. `gradle build` we don't want to fail because of code format,
 * so we disable checks for all tasks except those specified in [except]
 */
fun disableCodeFormattingChecks(vararg except: String) {
    project.gradle.taskGraph.whenReady {
        val taskNames = project.gradle.startParameter.taskNames
        if (!taskNames.any { it in except }) {
            allTasks.filter {
                it.name.contains("ktlint", true)
            }.forEach {
                it.enabled = false
            }
        }
    }
}
