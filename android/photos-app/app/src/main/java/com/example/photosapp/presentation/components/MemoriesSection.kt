package com.example.photosapp.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.photosapp.presentation.theme.PhotosAppTheme

/**
 * Data class representing a memory/featured item.
 */
data class Memory(
    val id: Long,
    val title: String,
    val imageUri: Uri?,
    val backgroundColor: Color = Color.Gray
)

/**
 * Horizontal scrolling section displaying memory cards.
 */
@Composable
fun MemoriesSection(
    memories: List<Memory>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        memories.forEach { memory ->
            MemoryCard(memory = memory)
        }
    }
}

/**
 * Individual memory card with rounded corners, background image, and text overlay.
 */
@Composable
private fun MemoryCard(
    memory: Memory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(180.dp)
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(16.dp))
            .background(memory.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        if (memory.imageUri != null) {
            AsyncImage(
                model = memory.imageUri,
                contentDescription = memory.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Semi-transparent gradient overlay for text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Centered text overlay
        Text(
            text = memory.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MemoriesSectionPreview() {
    val sampleMemories = listOf(
        Memory(id = 1, title = "DEC\n2016", imageUri = null, backgroundColor = Color(0xFF6B5B95)),
        Memory(id = 2, title = "JAN\n2017", imageUri = null, backgroundColor = Color(0xFF88B04B)),
        Memory(id = 3, title = "MAR\n2018", imageUri = null, backgroundColor = Color(0xFFF7CAC9)),
        Memory(id = 4, title = "JUL\n2019", imageUri = null, backgroundColor = Color(0xFF92A8D1))
    )

    PhotosAppTheme {
        MemoriesSection(memories = sampleMemories)
    }
}

@Preview(showBackground = true)
@Composable
private fun MemoryCardPreview() {
    PhotosAppTheme {
        MemoryCard(
            memory = Memory(
                id = 1,
                title = "DEC\n2016",
                imageUri = null,
                backgroundColor = Color(0xFF6B5B95)
            )
        )
    }
}
