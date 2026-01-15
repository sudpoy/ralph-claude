package com.example.photosapp.domain.usecase

import com.example.photosapp.domain.model.Photo
import com.example.photosapp.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving photos from the repository.
 * Encapsulates the business logic for fetching photos.
 */
class GetPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    /**
     * Invokes the use case to get all photos.
     * @return Flow emitting list of photos sorted by date taken (newest first)
     */
    operator fun invoke(): Flow<List<Photo>> {
        return photoRepository.getPhotos()
    }
}
