// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    id("io.gitlab.arturbosch.detekt").version("1.23.6")
}



subprojects {
        plugins.apply("io.gitlab.arturbosch.detekt")

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            outputs.cacheIf { false }
            this.jvmTarget = "1.8"
            dependsOn(":rules:assemble")
        }

        dependencies {
            detektPlugins(project(":rules"))
            detektPlugins("ru.kode:detekt-rules-compose:1.3.0")
            detektPlugins("io.nlopez.compose.rules:detekt:0.3.18")
        }
}
