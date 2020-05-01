repositories {
    jcenter()
}

val ktorVersion = "1.3.2"

dependencies {
    implementation("org.javacord:javacord:3.0.5")
    implementation("io.ktor:ktor-client:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2")
    implementation("com.beust:klaxon:5.0.1")
}