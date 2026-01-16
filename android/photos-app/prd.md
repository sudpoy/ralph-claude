# PRD: Google Photos Clone - Main Grid

## Introduction

Build a Google Photos-style Android app focusing on the main photo grid view. This is a learning project to study Android development using modern best practices: Jetpack Compose for UI and Clean Architecture for code organization. The app will display photos from the device's gallery in a grid layout with month/year grouping, similar to the Google Photos app.

## Goals

- Create a pixel-accurate recreation of the Google Photos main grid UI
- Load and display real photos from the device gallery using MediaStore API
- Implement Clean Architecture pattern for comprehensive Android learning
- Use Jetpack Compose for all UI components
- Group photos by month/year with section headers
- Include bottom navigation bar and top app bar with status indicators

## User Stories

### US-001: Set up Android project with Clean Architecture
**Description:** As a developer, I need a properly structured Android project so that I can build the app with clean separation of concerns.

**Acceptance Criteria:**
- [ ] Create new Android project with Kotlin and Jetpack Compose
- [ ] Set up package structure: `data/`, `domain/`, `presentation/`
- [ ] Add dependencies: Compose, Hilt, Coroutines, Coil (image loading)
- [ ] Configure Hilt for dependency injection
- [ ] Project builds successfully with `./gradlew assembleDebug`

### US-002: Create Photo domain model and repository interface
**Description:** As a developer, I need domain models and repository interfaces so that business logic is decoupled from data sources.

**Acceptance Criteria:**
- [ ] Create `Photo` data class in domain layer with: id, uri, dateTaken, width, height
- [ ] Create `PhotoRepository` interface in domain layer
- [ ] Create `GetPhotosUseCase` in domain layer
- [ ] Build passes (`./gradlew assembleDebug`)

### US-003: Implement MediaStore data source
**Description:** As a developer, I need to load photos from the device gallery so that users see their real photos.

**Acceptance Criteria:**
- [ ] Create `MediaStoreDataSource` in data layer
- [ ] Query MediaStore.Images for all photos on device
- [ ] Return photos sorted by date taken (newest first)
- [ ] Implement `PhotoRepositoryImpl` that uses MediaStoreDataSource
- [ ] Build passes (`./gradlew assembleDebug`)

### US-004: Request storage permissions
**Description:** As a user, I need to grant storage permissions so that the app can access my photos.

**Acceptance Criteria:**
- [ ] Add READ_MEDIA_IMAGES permission to AndroidManifest (API 33+)
- [ ] Add READ_EXTERNAL_STORAGE permission for older APIs
- [ ] Create permission request flow using Accompanist Permissions
- [ ] Show rationale dialog explaining why permission is needed
- [ ] Handle permission denied state with retry option
- [ ] Build passes and verify on emulator

### US-005: Create PhotosViewModel
**Description:** As a developer, I need a ViewModel to manage photo grid state so that UI is reactive and survives configuration changes.

**Acceptance Criteria:**
- [ ] Create `PhotosViewModel` using Hilt injection
- [ ] Expose `UiState` sealed class: Loading, Success(photos), Error(message)
- [ ] Expose photos grouped by month/year as `Map<String, List<Photo>>`
- [ ] Load photos on init, emit states via StateFlow
- [ ] Build passes (`./gradlew assembleDebug`)

### US-006: Create bottom navigation bar
**Description:** As a user, I want a bottom navigation bar so that I can navigate between app sections.

**Acceptance Criteria:**
- [ ] Create `BottomNavBar` composable with 4 items: Photos, Collections, Create, Ask
- [ ] Use Material 3 NavigationBar component
- [ ] Icons match Google Photos: grid, albums, add-circle, sparkle/AI icon
- [ ] "Photos" tab is selected by default with filled icon
- [ ] Unselected tabs show outlined icons
- [ ] Build passes and verify visually on emulator

### US-007: Create top app bar with backup status
**Description:** As a user, I want to see backup status and quick actions in the top bar.

**Acceptance Criteria:**
- [ ] Create `TopBar` composable matching mock design
- [ ] Show "Backup complete" chip with cloud icon on left
- [ ] Show add (+) button, notification bell, and profile avatar on right
- [ ] Profile avatar is circular with border
- [ ] Use proper spacing and alignment matching mock
- [ ] Build passes and verify visually on emulator

### US-008: Create memories/featured section
**Description:** As a user, I want to see featured memories at the top of my photos grid.

**Acceptance Criteria:**
- [ ] Create `MemoriesSection` composable with horizontal scrolling
- [ ] Display large rounded rectangle cards (aspect ratio ~3:4)
- [ ] Cards show background image with text overlay ("DEC", "2016")
- [ ] Text uses large bold font, positioned at center
- [ ] Cards have rounded corners (~16dp radius)
- [ ] Show 3-4 cards visible, horizontally scrollable
- [ ] Build passes and verify visually on emulator

### US-009: Create month section header
**Description:** As a user, I want to see month headers so I can navigate photos by time period.

**Acceptance Criteria:**
- [ ] Create `MonthHeader` composable matching mock
- [ ] Show month name on left ("January")
- [ ] Show checkmark icon and three-dot menu on right
- [ ] Use proper typography (medium weight, ~18sp)
- [ ] Proper vertical padding above and below
- [ ] Build passes and verify visually on emulator

### US-010: Create photo grid with thumbnails
**Description:** As a user, I want to see my photos in a grid layout so I can browse them easily.

**Acceptance Criteria:**
- [ ] Create `PhotoGrid` composable using LazyVerticalGrid
- [ ] Display 4 columns of square thumbnails
- [ ] Small gap between thumbnails (~2dp)
- [ ] Load images using Coil with placeholder
- [ ] Show thumbnails edge-to-edge (no outer padding)
- [ ] Build passes and verify visually on emulator

### US-011: Add video and favorite indicators on thumbnails
**Description:** As a user, I want to see which items are videos and which are favorites.

**Acceptance Criteria:**
- [ ] Show play icon overlay on video thumbnails (top-right)
- [ ] Show duration text on videos (e.g., "0:07")
- [ ] Show heart icon on favorited photos (bottom-left)
- [ ] Show star/count badge where applicable
- [ ] Icons have semi-transparent background for visibility
- [ ] Build passes and verify visually on emulator

### US-012: Assemble main screen with all components
**Description:** As a user, I want to see the complete Photos screen with all sections.

**Acceptance Criteria:**
- [ ] Create `PhotosScreen` composable combining all components
- [ ] Layout order: TopBar, MemoriesSection, scrollable content (headers + grids)
- [ ] BottomNavBar fixed at bottom
- [ ] Photos grouped by month with MonthHeader before each group
- [ ] Smooth scrolling through all content
- [ ] Build passes and verify matches mock on emulator

### US-013: Handle empty and loading states
**Description:** As a user, I want to see appropriate feedback when photos are loading or unavailable.

**Acceptance Criteria:**
- [ ] Show loading spinner while photos are being fetched
- [ ] Show empty state message if no photos found
- [ ] Show error state with retry button if loading fails
- [ ] States are visually centered and clear
- [ ] Build passes and verify on emulator

## Functional Requirements

- FR-1: App must request and handle READ_MEDIA_IMAGES permission (API 33+) or READ_EXTERNAL_STORAGE (older)
- FR-2: App must load all images from device MediaStore sorted by date descending
- FR-3: Photos must be grouped by month/year (e.g., "January 2024", "December 2023")
- FR-4: Photo grid must display 4 columns of square thumbnails
- FR-5: Bottom navigation must have 4 tabs: Photos, Collections, Create, Ask
- FR-6: Top bar must show backup status chip, add button, notification icon, and profile avatar
- FR-7: Memories section must show horizontally scrollable featured cards
- FR-8: Each month section must have a header with month name and action icons
- FR-9: Video thumbnails must show play icon and duration overlay
- FR-10: App must handle loading, success, empty, and error states gracefully

## Non-Goals

- No photo selection mode (multi-select with checkmarks)
- No actual backup functionality (UI only)
- No photo detail/fullscreen view
- No Collections, Create, or Ask tab implementations (just navigation UI)
- No search functionality
- No photo editing or sharing
- No cloud sync or account management
- No actual notification functionality

## Design Considerations

- **Mock reference:** `/Users/songbojin/coding/ralph-claude/android/photos-app/mocks/main.PNG`
- **Color scheme:** Light theme matching Google Photos (white background, blue accents)
- **Typography:** Use Material 3 default typography
- **Icons:** Use Material Icons, match Google Photos style (outlined for unselected, filled for selected)
- **Spacing:** Follow Material Design 3 spacing guidelines
- **Image loading:** Use Coil for efficient image loading with caching

## Technical Considerations

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Architecture:** Clean Architecture with three layers:
  - `presentation/` - Composables, ViewModels, UI state
  - `domain/` - Use cases, repository interfaces, domain models
  - `data/` - Repository implementations, data sources, DTOs
- **DI:** Hilt for dependency injection
- **Async:** Kotlin Coroutines and Flow
- **Image loading:** Coil Compose
- **Permissions:** Accompanist Permissions library
- **State management:** StateFlow in ViewModels

## Success Metrics

- App visually matches the Google Photos mock at first glance
- Photos load within 2 seconds on a mid-range device
- Smooth scrolling at 60fps through photo grid
- All 13 user stories pass acceptance criteria
- Clean separation of concerns - each layer only depends on appropriate layers

## Open Questions

- Should we add pull-to-refresh to reload photos?
- Should month headers be sticky (stay visible while scrolling)?
- What placeholder image should we show while photos load?
