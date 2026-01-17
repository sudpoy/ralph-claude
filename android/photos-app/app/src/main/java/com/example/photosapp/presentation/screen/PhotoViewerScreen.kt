package com.example.photosapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.photosapp.domain.model.Photo

/**
 * Full-screen photo viewer screen.
 * Displays a single photo centered on a black background,
 * maintaining aspect ratio without cropping or stretching.
 *
 * @param photo The photo to display
 * @param modifier Optional modifier
 */
@Composable
fun PhotoViewerScreen(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri)
                .crossfade(true)
                .build(),
            contentDescription = if (photo.isVideo) "Video ${photo.id}" else "Photo ${photo.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
