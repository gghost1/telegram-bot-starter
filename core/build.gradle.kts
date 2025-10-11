plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
    `maven-publish`
}

description = "core"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
        withJavadocJar()
    }
}

tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.5.6")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    api("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "ru.gghost.telegram"
            artifactId = "core"
            version = project.version.toString()

            pom {
                name.set("Telegram Core")
                description.set("Spring Boot Core for Telegram integration")
                url.set("https://github.com/gghost1/telegram-bot-starter")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("gghost1")
                        name.set("Dmitrii Antipov")
                    }
                }
                scm {
                    url.set("https://github.com/gghost1/telegram-bot-starter")
                }
            }
        }
    }

    repositories {
        mavenLocal()

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gghost1/telegram-bot-starter")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "gghost1"
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}