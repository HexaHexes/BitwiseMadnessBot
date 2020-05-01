import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    jcenter()
}

dependencies {
    implementation("org.javacord:javacord:3.0.5")
    implementation("io.ktor:ktor-client:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2")
    implementation("com.beust:klaxon:5.0.1")
    implementation("io.ktor:ktor-client-apache:1.3.2")
}