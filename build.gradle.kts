plugins {
    id("java")
}

group = "src"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(files("lib/lombok.jar"))
    implementation(files("lib/postgresql-42.6.0.jar"))
    implementation(files("lib/servlet-api.jar"))
    implementation(files("lib/openpdf-1.3.30.jar"))
    implementation(files("lib/openpdf-fonts-extra-1.3.30.jar"))

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.test {
    useJUnitPlatform()
}