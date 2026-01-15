package com.example.photosapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photosapp.domain.model.Photo
import com.example.photosapp.presentation.theme.PhotosAppTheme

/**
 * Photo grid displaying thumbnails in a 4-column layout.
 */
@Composable
fun PhotoGrid(
    photos: List<Photo>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(
            items = photos,
            key = { it.id }
        ) { photo ->
            PhotoThumbnail(photo = photo)
        }
    }
}

/**
 * Individual photo thumbnail displayed as a square.
 */
@Composable
private fun PhotoThumbnail(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri)
                .crossfade(true)
                .build(),
            contentDescription = "Photo ${photo.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Non-scrollable photo grid for embedding in a scrollable parent.
 * Calculates height based on photo count and displays all photos at once.
 */
@Composable
fun PhotoGridSection(
    photos: List<Photo>,
    modifier: Modifier = Modifier
) {
    val thumbnailSize = 90.dp // Approximate size for calculation
    val gap = 2.dp
    val columns = 4
    val rows = (photos.size + columns - 1) / columns
    val estimatedHeight = (thumbnailSize * rows) + (gap * (rows - 1).coerceAtLeast(0))

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .fillMaxWidth()
            .height(estimatedHeight),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
        userScrollEnabled = false
    ) {
        items(
            items = photos,
            key = { it.id }
        ) { photo ->
            PhotoThumbnail(photo = photo)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoGridPreview() {
    PhotosAppTheme {
        // Preview with empty list since we can't create Uri in preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray)
        )
    }
}
