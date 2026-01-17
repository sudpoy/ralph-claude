package com.example.photosapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photosapp.domain.model.Photo
import com.example.photosapp.presentation.theme.PhotosAppTheme

/**
 * Photo grid displaying thumbnails in a 4-column layout.
 *
 * @param photos List of photos to display
 * @param onPhotoClick Callback invoked when a photo is clicked, with the photo and its index
 * @param modifier Optional modifier for the grid
 */
@Composable
fun PhotoGrid(
    photos: List<Photo>,
    onPhotoClick: (photo: Photo, index: Int) -> Unit,
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
            val index = photos.indexOf(photo)
            PhotoThumbnail(
                photo = photo,
                onClick = { onPhotoClick(photo, index) }
            )
        }
    }
}

/**
 * Individual photo thumbnail displayed as a square with optional video/favorite indicators.
 *
 * @param photo The photo to display
 * @param onClick Callback invoked when the thumbnail is clicked
 * @param modifier Optional modifier for the thumbnail
 */
@Composable
fun PhotoThumbnail(
    photo: Photo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri)
                .crossfade(true)
                .build(),
            contentDescription = if (photo.isVideo) "Video ${photo.id}" else "Photo ${photo.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Video indicator (top-right corner with duration)
        if (photo.isVideo) {
            VideoIndicator(
                duration = photo.duration,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            )
        }

        // Favorite indicator (bottom-left corner)
        if (photo.isFavorite) {
            FavoriteIndicator(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
            )
        }
    }
}

/**
 * Video indicator showing play icon and duration.
 */
@Composable
private fun VideoIndicator(
    duration: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Video",
            modifier = Modifier.size(12.dp),
            tint = Color.White
        )
        Text(
            text = formatDuration(duration),
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

/**
 * Favorite indicator showing heart icon.
 */
@Composable
private fun FavoriteIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(3.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Favorite",
            modifier = Modifier.size(12.dp),
            tint = Color.White
        )
    }
}

/**
 * Formats duration in milliseconds to M:SS or H:MM:SS format.
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

/**
 * Non-scrollable photo grid for embedding in a scrollable parent.
 * Calculates height based on photo count and displays all photos at once.
 *
 * @param photos List of photos to display
 * @param onPhotoClick Callback invoked when a photo is clicked, with the photo and its index
 * @param modifier Optional modifier for the grid section
 */
@Composable
fun PhotoGridSection(
    photos: List<Photo>,
    onPhotoClick: (photo: Photo, index: Int) -> Unit,
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
            val index = photos.indexOf(photo)
            PhotoThumbnail(
                photo = photo,
                onClick = { onPhotoClick(photo, index) }
            )
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
