plugins {
  id("java")
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("xyz.jpenilla.run-paper") version "2.2.0"
  id("net.kyori.indra.checkstyle") version "3.1.3"
  id("com.github.ben-manes.versions") version "0.49.0"
}

group = "dev.tehbrian"
version = "2.2.6"
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
  compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

  implementation("com.google.inject:guice:7.0.0")
  implementation("dev.tehbrian:tehlib-paper:0.5.0")
  implementation("cloud.commandframework:cloud-paper:1.8.4")
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
    fun moveToLibs(vararg patterns: String) {
      for (pattern in patterns) {
        relocate(pattern, "$libsPackage.$pattern")
      }
    }

    moveToLibs(
      "cloud.commandframework",
      "com.google",
      "dev.tehbrian.tehlib",
      "io.leangen",
      "jakarta.inject",
      "javax.annotation",
      "org.aopalliance",
      "org.checkerframework",
      "org.spongepowered",
      "org.yaml",
    )
  }

  runServer {
    minecraftVersion("1.20.2")
  }
}
