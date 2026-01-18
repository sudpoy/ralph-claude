# PRD: Simplify Top Bar

## Introduction

Remove unnecessary UI elements from the top bar to reduce visual clutter. The current top bar contains non-functional elements (backup status, add button, notifications) that serve no purpose in a photo gallery viewer app. This change will create a cleaner, more focused user experience.

## Goals

- Remove non-functional UI elements from the top bar
- Keep only the Profile Avatar for potential future settings access
- Maintain visual balance and proper alignment
- Simplify the codebase by removing unused callbacks

## User Stories

### US-001: Remove Backup Status Chip
**Description:** As a user, I don't want to see a fake "Backup complete" indicator since there's no backup functionality.

**Acceptance Criteria:**
- [ ] Remove BackupStatusChip composable from TopBar
- [ ] Remove BackupStatusChip function definition (can keep for future use or delete entirely)
- [ ] Build passes (`./gradlew assembleDebug`)

### US-002: Remove Add Button
**Description:** As a user, I don't need an Add button since this is a photo viewer, not a photo creator.

**Acceptance Criteria:**
- [ ] Remove Add (+) IconButton from TopBar
- [ ] Remove `onAddClick` parameter from TopBar composable
- [ ] Remove Icons.Filled.Add import if unused
- [ ] Build passes (`./gradlew assembleDebug`)

### US-003: Remove Notifications Button
**Description:** As a user, I don't need a notifications bell since there's no notification system.

**Acceptance Criteria:**
- [ ] Remove Notifications IconButton from TopBar
- [ ] Remove `onNotificationClick` parameter from TopBar composable
- [ ] Remove Icons.Outlined.Notifications import if unused
- [ ] Build passes (`./gradlew assembleDebug`)

### US-004: Reposition Profile Avatar
**Description:** As a user, I want the Profile Avatar positioned appropriately in the simplified top bar.

**Acceptance Criteria:**
- [ ] Profile Avatar remains visible and tappable
- [ ] Top bar layout is balanced (avatar on the right)
- [ ] Proper padding maintained (16dp horizontal, 8dp vertical)
- [ ] Build passes (`./gradlew assembleDebug`)
- [ ] Verify visually on emulator with `./scripts/validate.sh US-004`

## Functional Requirements

- FR-1: Remove the BackupStatusChip from the TopBar layout
- FR-2: Remove the Add (+) button and its click handler
- FR-3: Remove the Notifications bell button and its click handler
- FR-4: Keep ProfileAvatar positioned on the right side of the TopBar
- FR-5: Maintain consistent padding and alignment

## Non-Goals

- No changes to the ProfileAvatar appearance
- No new functionality added to the top bar
- No changes to other screens or components

## Technical Considerations

- TopBar.kt is the only file that needs modification
- Remove unused imports after removing components
- Keep ProfileAvatar composable and its onClick handler for future use
- Preview composables may need updating

## Success Metrics

- Top bar contains only the Profile Avatar
- Visual appearance is clean and uncluttered
- App builds and runs without errors
- No unused code or imports remain

## Open Questions

- Should the Profile Avatar be left-aligned or right-aligned in the empty space?
  - **Decision:** Keep right-aligned for consistency with common app patterns
