import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

group = "dev.deftu"
version = "0.1.2"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

val testA by sourceSets.creating
val testB by sourceSets.creating

kotlinVersion("1.5.21", isPrimaryVersion = true)
kotlinVersion("1.6.20")
kotlinVersion("1.9.0")

dependencies {
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.21")
    implementation(kotlin("stdlib"))
    api("org.cadixdev:lorenz:0.5.8")
    runtimeOnly("net.java.dev.jna:jna:5.10.0") // don't strictly need this but IDEA spams log without

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")

    testRuntimeOnly(testA.output)
    testRuntimeOnly(testB.output)
    testRuntimeOnly("org.spongepowered:mixin:0.8.4")
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("remap")
}

publishing {
    publications {
        create("maven", MavenPublication::class) {
            from(components["java"])
        }
    }

    val publishingUsername: String? = run {
        return@run project.findProperty("deftu.publishing.username")?.toString() ?: System.getenv("DEFTU_PUBLISHING_USERNAME")
    }

    val publishingPassword: String? = run {
        return@run project.findProperty("deftu.publishing.password")?.toString() ?: System.getenv("DEFTU_PUBLISHING_PASSWORD")
    }

    repositories {
        mavenLocal()
        if (publishingUsername != null && publishingPassword != null) {
            fun MavenArtifactRepository.applyCredentials() {
                authentication.create<BasicAuthentication>("basic")
                credentials {
                    username = publishingUsername
                    password = publishingPassword
                }
            }

            maven {
                name = "DeftuReleases"
                url = uri("https://maven.deftu.dev/releases")
                applyCredentials()
            }

            maven {
                name = "DeftuSnapshots"
                url = uri("https://maven.deftu.dev/snapshots")
                applyCredentials()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

fun kotlinVersion(version: String, isPrimaryVersion: Boolean = false) {
    val name = version.replace(".", "")

    val sourceSet = sourceSets.create("kotlin$name")

    val testClasspath = configurations.create("kotlin${name}TestClasspath") {
        extendsFrom(configurations.testRuntimeClasspath.get())
        extendsFrom(configurations[sourceSet.compileOnlyConfigurationName])
    }

    dependencies {
        implementation(sourceSet.output)
        sourceSet.compileOnlyConfigurationName("org.jetbrains.kotlin:kotlin-compiler-embeddable:$version")
    }

    tasks.jar {
        from(sourceSet.output)
    }

    if (!isPrimaryVersion) {
        val testTask = tasks.register("testKotlin$name", Test::class) {
            useJUnitPlatform()
            testClassesDirs = sourceSets.test.get().output.classesDirs
            classpath = testClasspath + sourceSets.test.get().output + sourceSets.main.get().output
        }
        tasks.check { dependsOn(testTask) }
    }
}
