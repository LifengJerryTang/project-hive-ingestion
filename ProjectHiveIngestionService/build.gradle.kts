plugins {
    id("java")
    id("jacoco")
}

group = "com.projecthive.ingestion"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // === AWS Lambda Core ===
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")

    // === AWS DynamoDB ===
    implementation(platform("software.amazon.awssdk:bom:2.25.0"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // === Guice (Dependency Injection) ===
    implementation("com.google.inject:guice:7.0.0")
    implementation("javax.inject:javax.inject:1")

    // === Lombok ===
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // === Gmail API ===
    implementation("com.google.auth:google-auth-library-oauth2-http:1.21.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")

    // === Jackson ===
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")

    // === Logging ===
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    // === Testing ===
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

the<JacocoPluginExtension>().apply {
    toolVersion = "0.8.11"
}

tasks.test {
    useJUnitPlatform()
}

val excludedFromCoverage = listOf(
    "**/exceptions/**",
    "**/constants/**",
    "**/models/**",
    "**/guice/**",
    "**/auth/**",
    "**/Main.class"
)

val filteredClassDirs = files(layout.buildDirectory.dir("classes/java/main")).asFileTree.matching {
    exclude(excludedFromCoverage)
}


tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(filteredClassDirs)
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.named("jacocoTestReport"))

    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(filteredClassDirs)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(layout.buildDirectory.file("jacoco/test.exec"))
}

tasks.named("build") {
    dependsOn("jacocoTestCoverageVerification")
}
