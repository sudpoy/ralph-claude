package com.example.photosapp.domain.repository

import com.example.photosapp.domain.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing photos.
 * Implementations provide the actual data source (e.g., MediaStore, network).
 */
interface PhotoRepository {
    /**
     * Returns a Flow of all photos available on the device.
     * Photos should be sorted by date taken (newest first).
     */
    fun getPhotos(): Flow<List<Photo>>
}
