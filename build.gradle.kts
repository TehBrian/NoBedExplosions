plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "xyz.tehbrian"
version = "2.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-snapshots"
    }
    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.thbn.me/snapshots/") {
        name = "thbn-snapshots"
    }
    maven("https://libraries.minecraft.net") {
        name = "minecraft"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

    implementation("com.google.inject:guice:5.0.1")

    implementation("cloud.commandframework:cloud-bukkit:1.6.1")
    implementation("me.lucko:commodore:1.11")

    implementation("net.kyori:adventure-platform-bukkit:4.0.1")
    implementation("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT")

    implementation("org.spongepowered:configurate-yaml:4.1.2")

    implementation("dev.tehbrian:tehlib-paper:0.1.0-SNAPSHOT")

    implementation("io.papermc:paperlib:1.0.6")
}

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
        relocate("org.spongepowered.configurate.yaml", "$libsPackage.configurate.yaml")
        relocate("dev.tehbrian.tehlib", "$libsPackage.tehlib")
    }
}
