plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.kyori.indra.checkstyle") version "2.0.6"
}

group = "xyz.tehbrian"
version = "2.0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc"
    }
    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.thbn.me/snapshots/") {
        name = "thbn-snapshots"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.6")

    implementation("com.google.inject:guice:5.0.1")

    implementation("cloud.commandframework:cloud-bukkit:1.6.1")
    implementation("me.lucko:commodore:1.11")

    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")

    implementation("dev.tehbrian:tehlib-paper:0.1.0-SNAPSHOT")
}

// disable checkstyle in tests
project.gradle.startParameter.excludedTaskNames.add("checkstyleTest")

tasks {
    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveBaseName.set("NoBedExplosions")
        archiveClassifier.set("")

        val libsPackage = "xyz.tehbrian.nobedexplosions.libs"
        relocate("io.papermc.lib", "$libsPackage.paperlib")
        relocate("com.google.inject", "$libsPackage.guice")
        relocate("cloud.commandframework", "$libsPackage.cloud")
        relocate("me.lucko.commodore", "$libsPackage.commodore")
        relocate("net.kyori.adventure", "$libsPackage.adventure")
        relocate("org.spongepowered.configurate", "$libsPackage.configurate")
        relocate("dev.tehbrian.tehlib", "$libsPackage.tehlib")
    }

    runServer {
        minecraftVersion("1.18.1")
    }
}
