import java.util.*

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

version = ("artifact.version" to "artifact.version.key").artifactVersion

val Pair<String, String>.artifactVersion: String
    get() = first.run(
        Properties().apply {
            second.run(properties::get).let {
                "user.home"
                    .run(System::getProperty)
                    .run { "$this$it" }
            }.run(::File)
                .inputStream()
                .use(::load)
        }::get
    ).toString()

repositories { mavenCentral() }

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)
    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
}

tasks.named<Test>("test") { useJUnitPlatform() }
