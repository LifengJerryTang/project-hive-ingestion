plugins {
    id("java")
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
    implementation("com.google.inject:guice:7.0.0") // latest as of now
    implementation("javax.inject:javax.inject:1")

    // === Lombok (reduce boilerplate) ===
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // === Gmail API (OAuth2 + Mail APIs) ===
    implementation("com.google.auth:google-auth-library-oauth2-http:1.21.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")

    // === Jackson (JSON serialization) ===
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
}

tasks.test {
    useJUnitPlatform()
}