plugins {
    java
}

group = "su.nightexpress.excellentcrates"
version = "6.6.1"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.nightexpressdev.com/releases")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(libs.nightcore)
    compileOnly(libs.protocollib)
    compileOnly(libs.packetevents)
    compileOnly(libs.placeholderapi)
}

java {
    withSourcesJar()
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release = 21
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
