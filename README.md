# JsonComposeViewer

JsonComposeViewer is a **Kotlin Multiplatform** component for rendering JSON data using **Jetpack Compose / Compose Multiplatform**.

It takes a `kotlinx.serialization.json.JsonElement` and renders it as a **collapsible, syntax‑colored tree**, with:

- **Objects and arrays** rendered as expandable containers
- **Primitives** (strings, numbers, booleans, null) color‑coded by type
- **Indentation driven by a CompositionLocal**, so nested content lines up cleanly
- A configurable **`JsonViewerStyle`** to control colors, spacing, and whether to show field/element counts

This repository contains:

- `core/` – the JsonComposeViewer library
- `sample/` – a small multiplatform sample app that demonstrates the viewer

---

## Features

- **Kotlin Multiplatform**:
  - `commonMain` UI built with Compose Multiplatform
  - Targets: Android, JVM desktop, and Wasm JS (via Compose Multiplatform)
- **Hierarchy viewer for JSON**:
  - Collapsible objects and arrays
  - Type‑based coloring for keys and values
  - Indentation managed by a `CompositionLocal` instead of manual parameters
- **Customizable styling** via `JsonViewerStyle`:
  - Colors for keys, strings, numbers, booleans, and nulls
  - Background, corner radius, border color/width
  - Indentation and vertical spacing
  - Font size
  - Expand/collapse icon color
  - **`showElementCount`** to toggle whether headers show the number of fields/elements

---

## Getting started

JsonComposeViewer expects a `JsonElement` from `kotlinx.serialization.json.Json`. A typical flow is:

1. Parse a JSON string into a `JsonElement`
2. Pass it to `JsonComposeViewer` from your Compose UI

Example:

```kotlin
import androidx.compose.runtime.Composable
import kotlinx.serialization.json.Json
import net.tactware.jsoncomposeviewer.core.JsonComposeViewer
import net.tactware.jsoncomposeviewer.core.JsonViewerStyle

@Composable
fun MyJsonScreen(jsonString: String) {
    val element = Json.parseToJsonElement(jsonString)

    JsonComposeViewer(
        jsonElement = element,
        style = JsonViewerStyle(
            // Customize as needed
            showElementCount = false, // default; set true to show counts
        ),
        initiallyExpanded = true
    )
}
```

> **Note:** Publishing coordinates for this library may change; for now, treat this repository as the source of truth and depend on it via a composite build or by copying the `core` module into your project.

---

## `JsonComposeViewer` API overview

The main entry point is the `JsonComposeViewer` composable:

- **Package**: `net.tactware.jsoncomposeviewer.core`

```kotlin
@Composable
fun JsonComposeViewer(
    jsonElement: JsonElement,
    modifier: Modifier = Modifier,
    style: JsonViewerStyle = JsonViewerStyle(),
    initiallyExpanded: Boolean = false
)
```

- **`jsonElement`** – The `JsonElement` to render (object, array, or primitive)
- **`modifier`** – Usual Compose `Modifier` for layout
- **`style`** – A `JsonViewerStyle` instance to tweak appearance
- **`initiallyExpanded`** – Whether nested objects/arrays start expanded

Internally, `JsonComposeViewer`:

- Provides an **indentation level** via a `CompositionLocal`
- Dispatches to internal composables:
  - `JsonObjectView` for `JsonObject`
  - `JsonArrayView` for `JsonArray`
  - `JsonPrimitiveView` for `JsonPrimitive`

You normally only use `JsonComposeViewer` and `JsonViewerStyle` directly.

---

## `JsonViewerStyle`

`JsonViewerStyle` lets you customize how the viewer looks:

- **Package**: `net.tactware.jsoncomposeviewer.core`

Key properties (see `core/src/commonMain/.../JsonViewerStyle.kt` for full list):

- **`keyColor`** – Color for JSON object keys
- **`stringColor`**, **`numberColor`**, **`booleanColor`**, **`nullColor`**
- **`backgroundColor`** – Background for collapsible headers
- **`indentation`** – Horizontal space added per nesting level
- **`spacing`** – Vertical spacing between entries
- **`borderColor`**, **`borderWidth`**, **`cornerRadius`**
- **`fontSize`**
- **`expandIconColor`**
- **`showElementCount`** – Controls header text:
  - Objects: `{3}` vs `{}`
  - Arrays: `[5]` vs `[]`

Example:

```kotlin
val style = JsonViewerStyle(
    showElementCount = true,   // show field/element counts in headers
    indentation = 20.dp,
    spacing = 6.dp
)

JsonComposeViewer(
    jsonElement = element,
    style = style,
    initiallyExpanded = false
)
```

---

## Sample app

The `sample` module demonstrates JsonComposeViewer on multiple platforms.

### Desktop (JVM)

- Entry point: `sample/src/jvmMain/kotlin/net/tactware/jsoncomposeviewer/sample/Main.kt`
- Main screen: `sample/src/commonMain/kotlin/net/tactware/jsoncomposeviewer/sample/JsonViewerSampleScreen.kt`

Run from the project root:

```bash
./gradlew :sample:run
```

Or run `MainKt` from your IDE in the `sample` module.

### Android

- Main activity: `sample/src/androidMain/kotlin/net/tactware/jsoncomposeviewer/sample/MainActivity.kt`
- It simply sets `JsonViewerSampleScreen()` as its content.

Open the project in Android Studio or IntelliJ IDEA and run the `sample` Android configuration on a device/emulator.

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

Unless otherwise specified, this project is provided under the license declared in the repository root (add or reference your license file/text here). 

