plugins {
    id("kotlin")
}

dependencies {
    compileOnly("io.gitlab.arturbosch.detekt:detekt-api:1.23.6")
}

kotlin {
    jvmToolchain(8)
}