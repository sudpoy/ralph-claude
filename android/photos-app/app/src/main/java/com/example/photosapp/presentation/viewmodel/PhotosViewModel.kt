package com.example.photosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosapp.domain.model.Photo
import com.example.photosapp.domain.usecase.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Sealed class representing the UI state for the photos screen.
 */
sealed class PhotosUiState {
    data object Loading : PhotosUiState()
    data class Success(
        val photos: List<Photo>,
        val photosByMonth: Map<String, List<Photo>>
    ) : PhotosUiState()
    data class Error(val message: String) : PhotosUiState()
}

/**
 * Sealed class representing navigation state for photo viewing.
 */
sealed class PhotoViewerNavState {
    data object Hidden : PhotoViewerNavState()
    data class Viewing(val initialIndex: Int) : PhotoViewerNavState()
}

/**
 * ViewModel for managing photo grid state.
 * Loads photos from the repository and groups them by month/year.
 */
@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotosUiState>(PhotosUiState.Loading)
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()

    private val _viewerNavState = MutableStateFlow<PhotoViewerNavState>(PhotoViewerNavState.Hidden)
    val viewerNavState: StateFlow<PhotoViewerNavState> = _viewerNavState.asStateFlow()

    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    init {
        loadPhotos()
    }

    /**
     * Loads photos from the repository and updates the UI state.
     */
    fun loadPhotos() {
        _uiState.value = PhotosUiState.Loading

        viewModelScope.launch {
            getPhotosUseCase()
                .catch { exception ->
                    _uiState.value = PhotosUiState.Error(
                        exception.message ?: "Failed to load photos"
                    )
                }
                .collect { photos ->
                    val groupedPhotos = groupPhotosByMonth(photos)
                    _uiState.value = PhotosUiState.Success(
                        photos = photos,
                        photosByMonth = groupedPhotos
                    )
                }
        }
    }

    /**
     * Groups photos by month and year.
     * @param photos List of photos to group
     * @return Map where key is "Month Year" (e.g., "January 2024") and value is list of photos
     */
    private fun groupPhotosByMonth(photos: List<Photo>): Map<String, List<Photo>> {
        return photos.groupBy { photo ->
            monthYearFormat.format(Date(photo.dateTaken))
        }
    }

    /**
     * Opens the full-screen photo viewer at the specified index.
     * @param photo The photo that was clicked
     */
    fun openPhotoViewer(photo: Photo) {
        val currentState = _uiState.value
        if (currentState is PhotosUiState.Success) {
            val index = currentState.photos.indexOf(photo)
            if (index >= 0) {
                _viewerNavState.value = PhotoViewerNavState.Viewing(initialIndex = index)
            }
        }
    }

    /**
     * Closes the full-screen photo viewer and returns to the gallery.
     */
    fun closePhotoViewer() {
        _viewerNavState.value = PhotoViewerNavState.Hidden
    }
}
