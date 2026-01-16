package com.example.photosapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photosapp.presentation.components.BottomNavBar
import com.example.photosapp.presentation.components.MemoriesSection
import com.example.photosapp.presentation.components.Memory
import com.example.photosapp.presentation.components.MonthHeader
import com.example.photosapp.presentation.components.NavItem
import com.example.photosapp.presentation.components.PhotoGridSection
import com.example.photosapp.presentation.components.TopBar
import com.example.photosapp.presentation.viewmodel.PhotosUiState
import com.example.photosapp.presentation.viewmodel.PhotosViewModel

/**
 * Main Photos screen combining all components:
 * - TopBar with backup status and action buttons
 * - MemoriesSection with horizontally scrollable featured content
 * - Photo grid grouped by month with headers
 * - BottomNavBar fixed at bottom
 *
 * The entire content area (MemoriesSection + month headers + photo grids)
 * scrolls smoothly as a single unit.
 */
@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel = hiltViewModel()
) {
    var selectedNavItem by remember { mutableStateOf(NavItem.Photos) }
    val uiState by viewModel.uiState.collectAsState()

    // Sample memories for display (placeholder until real data is available)
    val sampleMemories = remember {
        listOf(
            Memory(id = 1, title = "DEC\n2016", imageUri = null, backgroundColor = Color(0xFF6B5B95)),
            Memory(id = 2, title = "JAN\n2017", imageUri = null, backgroundColor = Color(0xFF88B04B)),
            Memory(id = 3, title = "MAR\n2018", imageUri = null, backgroundColor = Color(0xFFF7CAC9)),
            Memory(id = 4, title = "JUL\n2019", imageUri = null, backgroundColor = Color(0xFF92A8D1))
        )
    }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { paddingValues ->
        PhotosScreenContent(
            uiState = uiState,
            memories = sampleMemories,
            onRetry = { viewModel.loadPhotos() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Content area of the Photos screen with smooth scrolling.
 * Layout order:
 * 1. MemoriesSection (horizontally scrollable)
 * 2. Scrollable content containing month headers + photo grids
 */
@Composable
private fun PhotosScreenContent(
    uiState: PhotosUiState,
    memories: List<Memory>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Memories/Featured section at the top
        MemoriesSection(
            memories = memories,
            modifier = Modifier.fillMaxWidth()
        )

        // Display photo grids based on UI state
        when (uiState) {
            is PhotosUiState.Loading -> {
                LoadingContent()
            }
            is PhotosUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onRetry
                )
            }
            is PhotosUiState.Success -> {
                if (uiState.photos.isEmpty()) {
                    EmptyContent()
                } else {
                    PhotosByMonthContent(photosByMonth = uiState.photosByMonth)
                }
            }
        }
    }
}

/**
 * Loading state with centered spinner.
 * Shows a circular progress indicator while photos are being fetched.
 */
@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading photos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state when no photos are found.
 * Shows a photo icon with message explaining no photos were found.
 */
@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Photo,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No photos found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take some photos or add them to your device",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

/**
 * Error state with retry button.
 * Shows error message and allows user to retry loading photos.
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Photos grouped by month with MonthHeader before each section.
 * Each month section contains a header and a grid of photos.
 */
@Composable
private fun PhotosByMonthContent(
    photosByMonth: Map<String, List<com.example.photosapp.domain.model.Photo>>,
    modifier: Modifier = Modifier
) {
    photosByMonth.forEach { (monthYear, photos) ->
        MonthHeader(monthName = monthYear)
        PhotoGridSection(
            photos = photos,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
