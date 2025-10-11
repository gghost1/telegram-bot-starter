# Telegram Bot Library (ru.gghost.telegram)

## Overview
This repository provides a lightweight Telegram bot library for Spring Boot applications.
It simplifies building Telegram bots by:
- Automatically configuring command beans
- Loading states annotated with `@State` into a command tree
- Managing user state in memory
- Handling incoming updates from the Telegram API

The library consists of two main modules:
- **ru.gghost.telegram:core** – a wrapper over `org.telegram:telegrambots-spring-boot-starter`, which automatically configures command beans, delivers updates, and stores user state in memory.
- **ru.gghost.telegram:starter** – a Spring Boot starter for integrating the library into any Spring Boot application with minimal setup.

## Basic Configuration
`application.yaml`:
```yaml
telegram:
  bot:
    token: <your-telegram-bot-token>
    name: <your-telegram-bot-name>
    startState: <your-base-state (e.g. START)>
```

## Create a State Class
1.	Create a class for the initial state.
2.	Annotate it with `@State(<STATE_NAME>)`.
3.	Extend the `Command` class and implement required methods.

Example:
```kotlin
@State(StartCommand.START)
class StartCommand : Command() {

    companion object {
        const val START = "START"
    }

    override fun processMessageUpdate(context: Context) {
        context.sendMessage(
            context.sendMessage()
                .text(context.update().message.text)
        )
    }
}
```
All classes annotated with `@State` and extending `Command` are automatically loaded into the command tree, which is printed in the log on startup.

## Adding the Library to Your Project
Gradle (build.gradle.kts):
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/gghost1/telegram-bot-starter")
        credentials {
            username = "<your-github-name>"
            password = "<your-github-readOnly-token>" // readOnly
        }
    }

    mavenCentral()
}

dependencies {
    implementation("ru.gghost.telegram:starter:0.2.2")
}
```
Maven (pom.xml):
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/gghost1/telegram-bot-starter</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ru.gghost.telegram</groupId>
        <artifactId>starter</artifactId>
        <version>0.2.2</version>
    </dependency>
</dependencies>
```

## Minimal Bot Example
```kotlin
@State(StartCommand.START)
class StartCommand : Command() {

    companion object {
        const val START = "START"
    }

    override fun processMessageUpdate(context: Context) {
        context.sendMessage(
            context.sendMessage()
                .text(context.update().message.text)
        )
    }
}
```
Run the bot and it will echo any message it receives.

## Context

The Context object is passed to every command during processing.
It provides a wide range of functionality for interacting with the user and bot, including:
- Sending messages
- Accessing updates
- Managing user state

_The functionality will continue to expand as the library evolves._

## Features Summary
- Automatic command and state registration
- In-memory user state management
- Simple integration with Spring Boot
- Extendable Context for flexible bot behavior

## License

This project is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).