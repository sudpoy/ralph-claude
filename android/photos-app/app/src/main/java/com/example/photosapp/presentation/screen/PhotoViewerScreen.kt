package com.example.photosapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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

private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 5f
private const val DOUBLE_TAP_ZOOM = 2f

/**
 * Individual photo page within the pager.
 * Supports pinch-to-zoom and double-tap zoom gestures.
 */
@Composable
private fun PhotoPage(
    photo: Photo,
    modifier: Modifier = Modifier,
    onZoomChange: (Float) -> Unit = {}
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)

        // Calculate new offset based on zoom change
        // When zooming, we want to zoom toward the center of the pinch gesture
        val newOffset = if (newScale > MIN_ZOOM) {
            offset + panChange
        } else {
            Offset.Zero
        }

        scale = newScale
        offset = newOffset
        onZoomChange(newScale)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        // Toggle between 1x and 2x zoom on double-tap
                        if (scale > MIN_ZOOM) {
                            // Reset to 1x
                            scale = MIN_ZOOM
                            offset = Offset.Zero
                        } else {
                            // Zoom to 2x centered on tap point
                            scale = DOUBLE_TAP_ZOOM
                            // Calculate offset to center the tap point
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            offset = Offset(
                                x = (centerX - tapOffset.x) * (DOUBLE_TAP_ZOOM - 1),
                                y = (centerY - tapOffset.y) * (DOUBLE_TAP_ZOOM - 1)
                            )
                        }
                        onZoomChange(scale)
                    }
                )
            }
            .transformable(state = transformableState),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri)
                .crossfade(true)
                .build(),
            contentDescription = if (photo.isVideo) "Video ${photo.id}" else "Photo ${photo.id}",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
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
