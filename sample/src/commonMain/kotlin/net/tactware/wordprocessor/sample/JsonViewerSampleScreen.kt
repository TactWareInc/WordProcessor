package net.tactware.wordprocessor.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.tactware.wordprocessor.core.JsonComposeViewer
import net.tactware.wordprocessor.core.JsonViewerStyle

@Composable
fun JsonViewerSampleScreen(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
) {
    val sampleJson: JsonElement = remember {
        Json.parseToJsonElement(
            """
            {
              "name": "Jane Doe",
              "age": 29,
              "active": true,
              "address": {
                "street": "123 Main St",
                "city": "Springfield",
                "postalCode": "12345"
              },
              "hobbies": [
                "reading",
                "gaming",
                "hiking"
              ],
              "favorites": {
                "number": 42,
                "colors": ["blue", "green"],
                "languages": [
                  { "name": "Kotlin", "type": "JVM" },
                  { "name": "TypeScript", "type": "JS" }
                ]
              },
              "notes": null
            }
            """.trimIndent()
        )
    }

    MaterialTheme {
        Surface(modifier = modifier) {
            Box(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopStart
            ) {
                JsonComposeViewer(
                    jsonElement = sampleJson,
                    style = JsonViewerStyle(),
                    initiallyExpanded = true
                )
            }
        }
    }
}


