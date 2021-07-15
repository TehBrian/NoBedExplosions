plugins {
    id("java")
}

group = "xyz.tehbrian"
version = "1.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    // for development builds
    maven {
        name = "sonatype-oss-snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

    compileOnly("com.google.inject:guice:5.0.1")

    compileOnly("cloud.commandframework:cloud-bukkit:1.4.0")

    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")

    compileOnly("org.slf4j:slf4j-api:2.0.0-alpha1")

    implementation("org.spongepowered:configurate-yaml:4.1.1")
}

tasks.processResources {
    expand("version" to project.version)
}
