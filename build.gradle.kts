plugins {
    id("java")
    application
}

group = "br.com.dio"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("br.com.dio.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.liquibase:liquibase-core:4.29.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.projectlombok:lombok:1.18.34")

    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testImplementation("org.mockito:mockito-core:5.13.0")
    testRuntimeOnly("com.h2database:h2:2.3.232")
}

tasks.test {
    useJUnitPlatform()
}