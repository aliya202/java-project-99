plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "8.6"
    id ("io.sentry.jvm.gradle") version "5.3.0"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

sentry {
    includeSourceContext = true
    org = "aliya202"
    projectName = "java-project-99"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.security:spring-security-test:6.4.3")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("net.datafaker:datafaker:2.0.1")
    implementation("org.instancio:instancio-junit:3.3.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}