## WordProcessor

WordProcessor is a **Kotlin Multiplatform** library that provides low‑level **word‑oriented byte processing**, message holders, and streaming reassembly utilities for binary protocols.

It focuses on:

- **Configurable word sizes** (16/32/64‑bit)
- **Byte order control** (endianness)
- **Protocol‑aware processing** via pluggable handlers
- **Message holders and reassemblers** for streaming or chunked data

This repository contains:

- `core/` – the WordProcessor core library
- `sample/` – a small multiplatform sample app (work in progress)

---

## Features

- **Kotlin Multiplatform**:
  - `commonMain` logic shared across targets
  - Targets: Android and JVM (additional targets can be added)
- **Core word processing**:
  - `WordProcessor` interface with configurable `WordSize` and `ByteOrder`
  - Conversion between byte arrays and words represented as `Long`
  - Validation of input buffers
- **Protocol helpers**:
  - `ProtocolHandler` and implementations for raw and standard processing
  - Message holders (`Message`, `WordHolder`, etc.) for representing packets/frames
- **Streaming reassembly**:
  - `MessageReassembler` and implementations for feeding byte streams
  - Buffering of partial data and emission of complete `Message` instances

---

## Installation

Artifacts are published as `WordProcessor Core` to Maven Central.

Gradle (Kotlin DSL):

```kotlin
dependencies {
    implementation("net.tactware.wordprocessor:core:1.0.0")
}
```

Make sure you have Maven Central enabled:

```kotlin
repositories {
    mavenCentral()
}
```

---

## Getting started

### Creating a `WordProcessor`

```kotlin
import net.tactware.wordprocessor.core.ByteOrder
import net.tactware.wordprocessor.core.WordProcessorFactory
import net.tactware.wordprocessor.core.WordSize

val processor = WordProcessorFactory.create(
    wordSize = WordSize.WORD_32,
    byteOrder = ByteOrder.BIG_ENDIAN
)

val bytes: ByteArray = /* incoming data */
require(processor.validate(bytes))

val words: List<Long> = processor.extractWords(bytes)
```

### Using a `MessageReassembler`

```kotlin
import net.tactware.wordprocessor.reassembler.MessageReassembler
import net.tactware.wordprocessor.reassembler.MessageReassemblerFactory

val reassembler: MessageReassembler =
    MessageReassemblerFactory.fixedSize(
        expectedWordCount = 8, // example
        wordSize = WordSize.WORD_32,
        byteOrder = ByteOrder.BIG_ENDIAN
    )

incomingChunks.forEach { chunk ->
    val messages = reassembler.feed(chunk)
    messages.forEach { message ->
        // handle complete message
    }
}
```

Refer to the `core/src/commonMain/kotlin/net/tactware/wordprocessor` package for more details on available processors, handlers, and message types.

---

## Modules

- `core`:
  - `core/` – core word processing interfaces and factories (`WordProcessor`, `WordProcessorFactory`, `WordSize`, `ByteOrder`)
  - `bits/` – bit‑level extraction and manipulation helpers
  - `holder/` – message and word holder abstractions
  - `impl/` – concrete `WordProcessor` implementations (16/32/64‑bit)
  - `protocol/` – protocol handler abstractions and implementations
  - `reassembler/` – message reassembly APIs and implementations
- `sample`:
  - Multiplatform sample app demonstrating basic usage (may be evolving alongside the library API)

---

## Development

- **Build everything**:

```bash
./gradlew build
```

- **Run tests**:

```bash
./gradlew test
```

Open the project in IntelliJ IDEA or Android Studio for best Kotlin Multiplatform support.

Kotlin and Gradle versions are controlled via:

- `gradle/libs.versions.toml`
- `gradle.properties`

---

## License

Unless otherwise specified, this project is provided under the license declared in the repository root (for example, Apache‑2.0 as used in published artifacts). 

