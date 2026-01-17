package com.example.photosapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.photosapp.domain.model.Photo

/**
 * Full-screen photo viewer screen with horizontal paging.
 * Displays photos in a horizontal pager allowing swipe navigation.
 *
 * @param photos List of all photos to display
 * @param initialIndex Index of the photo to show first
 * @param modifier Optional modifier
 */
@Composable
fun PhotoViewerScreen(
    photos: List<Photo>,
    initialIndex: Int,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (photos.size - 1).coerceAtLeast(0)),
        pageCount = { photos.size }
    )

    // Track zoom level for each page to reset on navigation
    var currentZoom by remember { mutableFloatStateOf(1f) }

    // Reset zoom when page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            currentZoom = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { pageIndex ->
            val photo = photos[pageIndex]
            PhotoPage(photo = photo)
        }
    }
}

/**
 * Individual photo page within the pager.
 */
@Composable
private fun PhotoPage(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
