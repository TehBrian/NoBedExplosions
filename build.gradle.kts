plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "xyz.tehbrian"
version = "2.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
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
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

    implementation("com.google.inject:guice:5.0.1")

    implementation("cloud.commandframework:cloud-bukkit:1.5.0")
    implementation("me.lucko:commodore:1.10")

    implementation("net.kyori:adventure-platform-bukkit:4.0.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")

    implementation("org.spongepowered:configurate-yaml:4.1.1")

    implementation("dev.tehbrian:tehlib-paper:0.1.0-SNAPSHOT")

    implementation("io.papermc:paperlib:1.0.6")
}

tasks {
    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveBaseName.set("NoBedExplosions")

        relocate("me.lucko.commodore", "xyz.tehbrian.nobedexplosions.lib.commodore")
        relocate("io.papermc.lib", "xyz.tehbrian.nobedexplosions.lib.paperlib")
        relocate("net.kyori.adventure", "xyz.tehbrian.nobedexplosions.lib.adventure")
    }
}
