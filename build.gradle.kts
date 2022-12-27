plugins {
  id("java")
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("xyz.jpenilla.run-paper") version "2.0.1"
  id("net.kyori.indra.checkstyle") version "3.0.1"
  id("com.github.ben-manes.versions") version "0.44.0"
}

group = "xyz.tehbrian"
version = "2.2.5"
description = "Control bed and respawn anchor functionality across all of" +
    " your worlds, such as allowing sleep in the nether or the end!"

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
  mavenCentral()
  maven("https://papermc.io/repo/repository/maven-public/")
  maven("https://repo.thbn.me/releases/")
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

  implementation("com.google.inject:guice:5.1.0")
  implementation("dev.tehbrian:tehlib-paper:0.3.1")
  implementation("org.spongepowered:configurate-yaml:4.1.2")
}

tasks {
  assemble {
    dependsOn(shadowJar)
  }

  processResources {
    expand("version" to project.version, "description" to project.description)
  }

  base {
    archivesName.set("NoBedExplosions")
  }

  shadowJar {
    archiveClassifier.set("")

    val libsPackage = "${project.group}.${project.name}.libs"
    relocate("cloud.commandframework", "$libsPackage.cloud")
    relocate("com.google.inject", "$libsPackage.guice")
    relocate("dev.tehbrian.tehlib", "$libsPackage.tehlib")
    relocate("org.spongepowered.configurate", "$libsPackage.configurate")
  }

  runServer {
    minecraftVersion("1.19.3")
  }
}
