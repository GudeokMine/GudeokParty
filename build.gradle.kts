import org.gradle.configurationcache.extensions.capitalized

plugins {
    idea
    kotlin("jvm") version Dependency.Kotlin.Version
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io")}
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/")}
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("io.github.monun:kommand-api:2.14.0")
    implementation("io.github.monun:tap-api:4.7.3")
    compileOnly("io.github.ithotl:PlayerStats:1.7.2-SNAPSHOT")
    compileOnly(files("libs/LiteEco.jar"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:${Dependency.Paper.Version}-R0.1-SNAPSHOT")
    paperDevBundle("${Dependency.Paper.Version}-R0.1-SNAPSHOT")
}

extra.apply {
    set("pluginName", project.name.split('-').joinToString("") { it.capitalize() })
    set("packageName", project.name.replace("-", ""))
    set("kotlinVersion", Dependency.Kotlin.Version)
    set("paperVersion", Dependency.Paper.Version)
}

tasks {
    // generate plugin.yml
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerJar(
        classifier: String,
        source: Any
    ) = register<Copy>("build${classifier.capitalized()}Jar") {
        from(source)

        val prefix = project.name
        val plugins = rootProject.file("E:\\마크구덕공고서버\\server\\plugins")

        from(source)
        into(plugins)
    }

    registerJar("dev", jar)
    registerJar("reobf", reobfJar)
}

idea {
    module {
        excludeDirs.add(file(".server"))
    }
}
