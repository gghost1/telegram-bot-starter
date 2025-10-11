plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
    `maven-publish`
}

description = "starter"

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
    api(project(":core"))
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "ru.gghost.telegram"
            artifactId = "starter"
            version = project.version.toString()

            // Добавим описание и метаданные (для красоты)
            pom {
                name.set("Telegram Starter")
                description.set("Spring Boot Starter for Telegram integration")
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