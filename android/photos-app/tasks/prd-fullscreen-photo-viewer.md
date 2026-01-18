# PRD: Full-Screen Photo Viewer

## Introduction

Currently, photos in the gallery display only as small thumbnails with no interaction. Tapping a photo does nothing. This feature adds a full-screen photo viewer that allows users to view photos at full resolution, navigate between photos with swipe gestures, zoom in for detail, and dismiss elegantly with a drag-down gesture.

## Goals

- Enable tapping any photo thumbnail to open it in full-screen view
- Allow horizontal swiping to navigate between photos with smooth paging
- Support pinch-to-zoom, double-tap zoom, and pan when zoomed
- Show photo metadata (date, filename) with tap-to-hide overlay
- Provide elegant swipe-down-to-dismiss with scale/fade animation
- Maintain smooth 60fps performance during all gestures

## User Stories

### US-001: Add click handler to photo thumbnails
**Description:** As a user, I want to tap on any photo thumbnail so that I can open it in full-screen view.

**Acceptance Criteria:**
- [ ] PhotoThumbnail composable accepts an `onClick` callback parameter
- [ ] PhotoGrid passes the photo and its index to the click handler
- [ ] Tapping a thumbnail triggers navigation to full-screen view with the selected photo
- [ ] Build passes (`./gradlew assembleDebug`)

### US-002: Create full-screen photo viewer screen
**Description:** As a user, I want to see the selected photo displayed in full-screen so that I can view it in detail.

**Acceptance Criteria:**
- [ ] New `PhotoViewerScreen` composable created in presentation/screen/
- [ ] Photo displays full-screen with black background
- [ ] Photo maintains aspect ratio (no cropping or stretching)
- [ ] Photo is centered in the screen
- [ ] Uses Coil AsyncImage for loading with placeholder
- [ ] Supports both portrait and landscape orientations
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: tapping a photo opens full-screen view

### US-003: Implement horizontal paging between photos
**Description:** As a user, I want to swipe left/right to navigate between photos so that I can browse my gallery seamlessly.

**Acceptance Criteria:**
- [ ] Use HorizontalPager from Accompanist/Compose Foundation
- [ ] Swipe right shows next photo, swipe left shows previous photo
- [ ] Smooth paging animation with bounce effect at first/last photo
- [ ] Current photo index maintained when navigating
- [ ] Pager starts at the photo that was tapped
- [ ] Zoom resets to 1x when navigating to a different photo
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: swiping navigates between photos smoothly

### US-004: Add pinch-to-zoom and double-tap zoom
**Description:** As a user, I want to zoom into photos so that I can see fine details.

**Acceptance Criteria:**
- [ ] Pinch gesture zooms in/out smoothly
- [ ] Double-tap toggles between 1x and 2x zoom (or fits width)
- [ ] Minimum zoom is 1x (fit screen), maximum zoom is 5x
- [ ] Zoom is centered on the pinch/tap point
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: pinch and double-tap zoom work correctly

### US-005: Add pan gesture when zoomed
**Description:** As a user, I want to pan around when zoomed in so that I can see different parts of the photo.

**Acceptance Criteria:**
- [ ] When zoom > 1x, drag gesture pans the photo
- [ ] Pan is bounded to photo edges (cannot pan beyond the image)
- [ ] Pan has smooth momentum/fling behavior
- [ ] At 1x zoom, horizontal drag triggers paging instead of pan
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: panning works when zoomed, paging works when not zoomed

### US-006: Add photo metadata overlay
**Description:** As a user, I want to see photo information (date, filename) so that I know details about the photo I'm viewing.

**Acceptance Criteria:**
- [ ] Overlay shows at top of screen with semi-transparent background
- [ ] Displays photo date in readable format (e.g., "January 15, 2024")
- [ ] Displays filename or photo dimensions as secondary info
- [ ] Overlay visible by default when entering full-screen
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: metadata overlay displays correctly

### US-007: Implement tap-to-toggle overlay visibility
**Description:** As a user, I want to tap the screen to hide/show the metadata overlay so that I can view the photo without distractions.

**Acceptance Criteria:**
- [ ] Single tap anywhere on photo toggles overlay visibility
- [ ] Overlay animates in/out (fade animation)
- [ ] Tap detection doesn't interfere with zoom/pan gestures
- [ ] Status bar and navigation bar also hide/show with overlay (immersive mode)
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: tapping hides/shows overlay smoothly

### US-008: Implement swipe-down-to-dismiss gesture
**Description:** As a user, I want to drag the photo down to dismiss the full-screen view so that I can return to the gallery elegantly.

**Acceptance Criteria:**
- [ ] Dragging photo vertically (when not zoomed) starts dismiss gesture
- [ ] Photo scales down proportionally as it's dragged (e.g., 1.0 to 0.8 scale)
- [ ] Background fades from black to transparent as drag progresses
- [ ] Releasing after dragging > 100dp dismisses the viewer
- [ ] Releasing after dragging < 100dp snaps photo back to center with spring animation
- [ ] Dismiss navigates back to gallery with the grid visible
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: drag-to-dismiss works with scale/fade animation

### US-009: Wire up navigation between grid and viewer
**Description:** As a developer, I need to implement navigation state management so that the app can transition between gallery and viewer screens.

**Acceptance Criteria:**
- [ ] Navigation state managed in PhotosViewModel or separate navigation state
- [ ] Selected photo index passed to viewer screen
- [ ] Full photo list available in viewer for paging
- [ ] Back gesture/button returns to gallery
- [ ] Gallery scroll position preserved when returning from viewer
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify on emulator: full navigation flow works end-to-end

## Functional Requirements

- FR-1: PhotoThumbnail must accept an `onClick: (Photo, Int) -> Unit` parameter
- FR-2: PhotoViewerScreen must display a single photo full-screen with black background
- FR-3: HorizontalPager must contain all photos from the current gallery view
- FR-4: Zoom level must reset to 1x when paging to a different photo
- FR-5: Zoom range must be 1x (minimum) to 5x (maximum)
- FR-6: Double-tap must toggle between 1x and 2x zoom levels
- FR-7: Pan must only be active when zoom level > 1x
- FR-8: Pan boundaries must prevent showing empty space beyond photo edges
- FR-9: Metadata overlay must show date and secondary info (filename or dimensions)
- FR-10: Overlay visibility must toggle on single tap
- FR-11: Swipe-down dismiss must require > 100dp vertical drag to trigger
- FR-12: Dismiss animation must scale photo from 1.0 to ~0.8 and fade background
- FR-13: Spring animation must snap photo back if dismiss threshold not reached
- FR-14: Viewer must support both portrait and landscape orientations

## Non-Goals

- Video playback (videos will show thumbnail only, no play functionality)
- Photo editing (crop, rotate, filters)
- Sharing or deleting photos from viewer
- Photo EXIF data display (camera model, aperture, etc.)
- Thumbnail strip/filmstrip at bottom of viewer
- Page indicator (e.g., "3 of 45" or dots)
- Transition animation from thumbnail to full-screen (shared element)

## Design Considerations

- Use black background for full-screen to maximize photo visibility
- Overlay should use semi-transparent gradient (not solid) for elegance
- Zoom animations should use spring physics for natural feel
- Consider edge-to-edge display (content behind system bars)
- Follow Material 3 design language for any UI elements

## Technical Considerations

### Dependencies (may need to add):
- `androidx.compose.foundation:foundation` - for HorizontalPager (Compose 1.4+)
- Consider using existing Coil for image loading (already in project)

### Key Implementation Notes:
- PhotoThumbnail is in `presentation/components/PhotoGrid.kt`
- Photo model has `uri`, `dateTaken`, `width`, `height` fields
- App uses Clean Architecture - create viewer in presentation layer
- ViewModel already has `photosByMonth` map and flat `photos` list
- No Jetpack Navigation currently - use simple state-based navigation

### Gesture Handling Priority:
1. Pinch-to-zoom (highest priority)
2. Double-tap zoom
3. Pan when zoomed
4. Horizontal swipe for paging (when not zoomed)
5. Vertical swipe for dismiss (when not zoomed)
6. Single tap for overlay toggle (lowest priority, after other gestures timeout)

### Files to Create:
- `presentation/screen/PhotoViewerScreen.kt` - Main viewer composable
- `presentation/components/ZoomableImage.kt` - Reusable zoomable image component

### Files to Modify:
- `presentation/components/PhotoGrid.kt` - Add onClick to PhotoThumbnail
- `presentation/screen/PhotosScreen.kt` - Add navigation state and viewer
- `presentation/viewmodel/PhotosViewModel.kt` - Add selected photo state (if needed)

## Success Metrics

- Tapping a photo opens full-screen view within 200ms
- Paging between photos maintains 60fps
- Zoom gestures respond without perceptible lag
- Dismiss gesture feels natural and responsive
- No crashes or ANRs during gesture interactions

