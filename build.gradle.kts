import net.minecraftforge.gradle.userdev.UserDevExtension

val modName: String by ext.properties
val modGroup: String by ext.properties
val subVersion: String by ext.properties
val mcVersion: String by ext.properties
val mcpChannel: String by ext.properties
val mcpVersion: String by ext.properties
val forgeVersion: String by ext.properties
val repositoryLink: String by ext.properties
val kotlinVersion: String by ext.properties
val annotationsVersion: String by ext.properties
val coroutinesVersion: String by ext.properties
val serializationVersion: String by ext.properties
val modDescription: String by ext.properties

buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://maven.minecraftforge.net/")
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:5.1.+") {
            isChanging = true
        }
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

plugins {
    java
    kotlin("jvm") version "1.9.0"
    id("net.kyori.blossom") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

apply(plugin = "net.minecraftforge.gradle")

version = "$kotlinVersion.$subVersion"
group = modGroup

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

configure<UserDevExtension> {
    mappings(mcpChannel, mcpVersion)
}

val minecraft by configurations

dependencies {
    minecraft("net.minecraftforge:forge:$forgeVersion")

    shadow("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    shadow("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    shadow("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    shadow("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    shadow("org.jetbrains:annotations:$annotationsVersion")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
    shadow("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    shadow("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
}

blossom {
    replaceTokenIn("src/main/kotlin/io/github/chaosunity/forgelin/Forgelin.kt")
    replaceToken("@version@", "$kotlinVersion.$subVersion")
}

tasks {
    shadowJar {
        archiveBaseName.set(modName)
        archiveClassifier.set("")
        archiveVersion.set("$kotlinVersion.$subVersion")
        configurations = listOf(project.configurations.shadow.get())

        exclude("module-info.class", "META-INF/maven/**", "META-INF/proguard/**", "META-INF/versions/**")

        finalizedBy("reobfJar")
    }

    artifacts {
        archives(shadowJar)
        shadow(shadowJar)
    }

    processResources {
        filesMatching("mcmod.info") {
            expand(
                "version" to "$kotlinVersion.$subVersion",
                "mcversion" to mcVersion,
                "modname" to modName,
                "modid" to "forgelin",
                "modname" to modName,
                "link" to repositoryLink,
                "description" to modDescription
            )
        }
    }

    jar {
        manifest.attributes(
            "FMLCorePluginContainsFMLMod" to "true",
            "FMLCorePlugin" to "io.github.chaosunity.forgelin.preloader.ForgelinPlugin"
        )
    }
}
