package com.example.photosapp.presentation.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.photosapp.domain.model.Photo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// Dismiss threshold in dp - drag beyond this to dismiss
private const val DISMISS_THRESHOLD_DP = 100f
// Minimum scale when dragging to dismiss (0.8 = 80%)
private const val MIN_DISMISS_SCALE = 0.8f

/**
 * Full-screen photo viewer screen with horizontal paging.
 * Displays photos in a horizontal pager allowing swipe navigation.
 * Supports swipe-down-to-dismiss gesture.
 *
 * @param photos List of all photos to display
 * @param initialIndex Index of the photo to show first
 * @param onDismiss Callback when the viewer should be dismissed
 * @param modifier Optional modifier
 */
@Composable
fun PhotoViewerScreen(
    photos: List<Photo>,
    initialIndex: Int,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (photos.size - 1).coerceAtLeast(0)),
        pageCount = { photos.size }
    )

    // Track zoom level for each page to reset on navigation
    var currentZoom by remember { mutableFloatStateOf(1f) }

    // Track overlay visibility (visible by default when entering full-screen)
    var isOverlayVisible by remember { mutableStateOf(true) }

    // Dismiss gesture state
    val density = LocalDensity.current
    val dismissThresholdPx = with(density) { DISMISS_THRESHOLD_DP.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()
    val dismissOffsetY = remember { Animatable(0f) }

    // Calculate dismiss progress (0 = no drag, 1 = at threshold)
    val dismissProgress = (abs(dismissOffsetY.value) / dismissThresholdPx).coerceIn(0f, 1f)
    // Scale decreases from 1.0 to MIN_DISMISS_SCALE as dismissProgress increases
    val dismissScale = 1f - (dismissProgress * (1f - MIN_DISMISS_SCALE))
    // Background alpha decreases from 1.0 to 0.0 as dismissProgress increases
    val backgroundAlpha = 1f - dismissProgress

    // Set up immersive mode controller
    val view = LocalView.current
    val window = (view.context as? Activity)?.window

    // Handle immersive mode based on overlay visibility
    DisposableEffect(isOverlayVisible) {
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isOverlayVisible) {
                // Show system bars when overlay is visible
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                // Hide system bars when overlay is hidden
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }

        onDispose {
            // Restore system bars when leaving the screen
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Reset zoom when page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            currentZoom = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = dismissScale
                    scaleY = dismissScale
                    translationY = dismissOffsetY.value
                }
                .pointerInput(currentZoom) {
                    // Only allow dismiss gesture when not zoomed
                    if (currentZoom <= MIN_ZOOM) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (abs(dismissOffsetY.value) > dismissThresholdPx) {
                                        // Dismiss the viewer
                                        onDismiss()
                                    } else {
                                        // Snap back to center with spring animation
                                        dismissOffsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    dismissOffsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            },
                            onVerticalDrag = { _, dragAmount ->
                                coroutineScope.launch {
                                    dismissOffsetY.snapTo(dismissOffsetY.value + dragAmount)
                                }
                            }
                        )
                    }
                },
            beyondViewportPageCount = 1,
            // Disable paging when zoomed in to allow panning
            userScrollEnabled = currentZoom <= MIN_ZOOM
        ) { pageIndex ->
            val photo = photos[pageIndex]
            PhotoPage(
                photo = photo,
                onZoomChange = { zoom -> currentZoom = zoom },
                onTap = { isOverlayVisible = !isOverlayVisible }
            )
        }

        // Metadata overlay at top of screen with fade animation
        val currentPhoto = photos.getOrNull(pagerState.currentPage)
        if (currentPhoto != null) {
            AnimatedVisibility(
                visible = isOverlayVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                PhotoMetadataOverlay(photo = currentPhoto)
            }
        }
    }
}

/**
 * Overlay displaying photo metadata at the top of the screen.
 * Shows date in readable format and photo dimensions.
 */
@Composable
private fun PhotoMetadataOverlay(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(photo.dateTaken) {
        dateFormat.format(Date(photo.dateTaken))
    }
    val dimensions = "${photo.width} × ${photo.height}"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Text(
            text = dimensions,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 5f
private const val DOUBLE_TAP_ZOOM = 2f

/**
 * Individual photo page within the pager.
 * Supports pinch-to-zoom, double-tap zoom, pan gestures, and single tap to toggle overlay.
 */
@Composable
private fun PhotoPage(
    photo: Photo,
    modifier: Modifier = Modifier,
    onZoomChange: (Float) -> Unit = {},
    onTap: () -> Unit = {}
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val containerWidthPx = with(density) { maxWidth.toPx() }
        val containerHeightPx = with(density) { maxHeight.toPx() }

        /**
         * Calculate the maximum allowed offset based on current scale.
         * When zoomed in, the photo can be panned within the bounds of the scaled content.
         * The formula: maxOffset = (scaledSize - containerSize) / 2
         * This ensures the photo edges cannot be panned past the container edges.
         */
        fun calculateMaxOffset(currentScale: Float): Offset {
            // When scaled, the content extends beyond the container
            // We can pan up to half the extra width/height in each direction
            val maxOffsetX = ((containerWidthPx * currentScale - containerWidthPx) / 2f).coerceAtLeast(0f)
            val maxOffsetY = ((containerHeightPx * currentScale - containerHeightPx) / 2f).coerceAtLeast(0f)
            return Offset(maxOffsetX, maxOffsetY)
        }

        /**
         * Clamp the offset to prevent panning beyond photo edges.
         */
        fun clampOffset(rawOffset: Offset, currentScale: Float): Offset {
            val maxOffset = calculateMaxOffset(currentScale)
            return Offset(
                x = rawOffset.x.coerceIn(-maxOffset.x, maxOffset.x),
                y = rawOffset.y.coerceIn(-maxOffset.y, maxOffset.y)
            )
        }

        val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
            val newScale = (scale * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)

            // Calculate new offset based on zoom change
            val newOffset = if (newScale > MIN_ZOOM) {
                // When zooming, adjust offset to maintain zoom focus point
                // Then add the pan change for dragging
                val scaledOffset = offset * (newScale / scale) + panChange
                clampOffset(scaledOffset, newScale)
            } else {
                Offset.Zero
            }

            scale = newScale
            offset = newOffset
            onZoomChange(newScale)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // Single tap toggles overlay visibility
                            onTap()
                        },
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
                                val rawOffset = Offset(
                                    x = (centerX - tapOffset.x) * (DOUBLE_TAP_ZOOM - 1),
                                    y = (centerY - tapOffset.y) * (DOUBLE_TAP_ZOOM - 1)
                                )
                                // Clamp the offset to photo bounds
                                val maxOffset = calculateMaxOffset(DOUBLE_TAP_ZOOM)
                                offset = Offset(
                                    x = rawOffset.x.coerceIn(-maxOffset.x, maxOffset.x),
                                    y = rawOffset.y.coerceIn(-maxOffset.y, maxOffset.y)
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
}
