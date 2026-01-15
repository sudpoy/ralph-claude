# UI Validation Guide for Ralph

This guide explains how to validate Android UI stories using screenshots and AI vision comparison.

## Overview

For each UI story, Ralph must:
1. Build and install the app
2. Navigate to the relevant screen
3. Capture screenshots
4. Compare screenshots to the mock using AI vision
5. Verify acceptance criteria are met
6. Mark story as `passes: true` only if all criteria pass

## Validation Workflow

### Step 1: Run Validation Script

```bash
./scripts/validate.sh <STORY_ID>
```

This will:
- Build the app
- Install on emulator
- Launch the app
- Grant permissions if needed
- Take screenshots
- Generate a validation report

### Step 2: Read Screenshots

After the script completes, read the captured screenshots:

```
Read /path/to/android/photos-app/screenshots/<STORY_ID>/<STORY_ID>_main_<timestamp>.png
```

### Step 3: Read Mock Reference

Read the mock to compare against:

```
Read /path/to/android/photos-app/mocks/main.PNG
```

### Step 4: AI Vision Comparison

Compare the screenshot to the mock and verify:

1. **Layout Match**: Overall structure matches (top bar, content, bottom nav)
2. **Element Presence**: All required UI elements are visible
3. **Styling**: Colors, fonts, spacing appear correct
4. **Content**: Photos/images load properly (not blank)
5. **State**: Correct state shown (selected tabs, icons, etc.)

### Step 5: Document Findings

In progress.txt, document:
- What was verified
- Any discrepancies found
- Screenshots compared

### Step 6: Update PRD

Only if ALL acceptance criteria pass:
- Edit `prd.json`
- Set `passes: true` for the story
- Add notes about what was verified

## UI Navigation Commands

For complex validations requiring navigation, use the UI actions:

```bash
# Source the helpers
source ./scripts/ui-actions.sh

# Tap at coordinates
tap 500 300

# Tap at percentage of screen (center = 50, 50)
tap_percent 50 50

# Scroll down
scroll_down

# Scroll horizontally (for memories section)
scroll_left

# Tap bottom navigation (0 = first tab)
tap_bottom_nav 0
tap_bottom_nav 1
tap_bottom_nav 2
tap_bottom_nav 3

# Take screenshot
take_screenshot /path/to/output.png

# Grant permission dialog
grant_permission

# Press back
press_back
```

## Story-Specific Validation

### US-006: Bottom Navigation Bar

**Navigate:** App launch (default screen)

**Verify:**
- [ ] Navigation bar visible at bottom of screen
- [ ] 4 items: Photos, Collections, Create, Ask
- [ ] "Photos" tab is selected (filled icon, label highlighted)
- [ ] Other tabs have outlined icons
- [ ] Icons match Google Photos style

**Custom validation script:**
```bash
# Tap each tab and screenshot
tap_bottom_nav 0
take_screenshot screenshots/US-006/photos_tab.png

tap_bottom_nav 1
take_screenshot screenshots/US-006/collections_tab.png

tap_bottom_nav 2
take_screenshot screenshots/US-006/create_tab.png

tap_bottom_nav 3
take_screenshot screenshots/US-006/ask_tab.png
```

### US-007: Top App Bar

**Navigate:** App launch (default screen)

**Verify:**
- [ ] Top bar visible at top of screen
- [ ] "Backup complete" chip with cloud icon (left side)
- [ ] Add (+) button visible
- [ ] Notification bell icon visible
- [ ] Profile avatar (circular with border) on right

### US-008: Memories Section

**Navigate:** App launch, look below top bar

**Verify:**
- [ ] Horizontal row of large cards visible
- [ ] Cards have ~3:4 aspect ratio
- [ ] Rounded corners on cards (~16dp)
- [ ] Text overlay showing month/year (e.g., "DEC", "2016")
- [ ] Can scroll horizontally

**Custom validation:**
```bash
# Scroll memories section
scroll_left  # May need coordinates for just that section
take_screenshot screenshots/US-008/memories_scrolled.png
```

### US-009: Month Headers

**Navigate:** Scroll down past memories section

**Verify:**
- [ ] Month name visible (e.g., "January")
- [ ] Checkmark icon on right side
- [ ] Three-dot menu icon on right side
- [ ] Proper typography and spacing

### US-010: Photo Grid

**Navigate:** Below month headers

**Verify:**
- [ ] 4 columns of square thumbnails
- [ ] Small gaps between photos (~2dp)
- [ ] Photos actually load (not blank/gray placeholders)
- [ ] Edge-to-edge layout (no outer padding)

### US-011: Video/Favorite Indicators

**Navigate:** Photo grid area

**Verify:**
- [ ] Video thumbnails have play icon (top-right or bottom-left)
- [ ] Duration text visible on videos (e.g., "0:07")
- [ ] Heart/favorite icon on favorited items
- [ ] Icons have semi-transparent background for contrast

### US-012: Full Screen Assembly

**Navigate:** Full app view

**Verify:**
- [ ] TopBar at top
- [ ] Memories section below top bar
- [ ] Month headers above photo sections
- [ ] Photo grid fills main content area
- [ ] Bottom navigation bar at bottom
- [ ] Smooth scrolling works
- [ ] Overall appearance matches mock

### US-013: Loading/Empty/Error States

**Navigate:** App launch with various conditions

**To test loading state:**
```bash
# Clear app data and relaunch
clear_app_data com.example.photosapp
launch_app com.example.photosapp com.example.photosapp.MainActivity
# Quickly take screenshot before loading completes
take_screenshot screenshots/US-013/loading_state.png
```

**To test empty state:**
- Use emulator with no photos
- Or mock the repository to return empty list

**Verify:**
- [ ] Loading: Spinner/progress indicator visible
- [ ] Empty: Message like "No photos yet" centered
- [ ] Error: Error message with "Retry" button

## Validation Failure Handling

If validation fails:

1. **Do NOT mark as `passes: true`**
2. Document the specific failure in progress.txt
3. Note which acceptance criteria failed
4. Describe what the screenshot showed vs. expected
5. The next Ralph iteration will attempt to fix the issue

## Example Validation Session

```markdown
## Validation: US-006 - Bottom Navigation Bar

### Screenshots Compared:
- App screenshot: screenshots/US-006/US-006_main_20240115_143022.png
- Mock reference: mocks/main.PNG

### Verification:
✅ Navigation bar visible at bottom
✅ 4 tabs present (Photos, Collections, Create, Ask)
✅ Photos tab selected with filled icon
✅ Other tabs show outlined icons
⚠️ Icon for "Ask" slightly different from mock (using star instead of sparkle)

### Result: PARTIAL PASS
Need to update Ask icon to match Google Photos sparkle/AI icon.

### Action:
- NOT marking as passes: true
- Created follow-up task to fix Ask icon
```

## Tips for Accurate Comparison

1. **Focus on structure first** - Is the layout correct?
2. **Check relative positions** - Are elements in the right places?
3. **Verify colors approximately** - Exact hex match isn't required
4. **Look for missing elements** - What should be there but isn't?
5. **Check text content** - Are labels correct?
6. **Verify states** - Selected/unselected, enabled/disabled
7. **Allow minor differences** - Small spacing variations are OK
8. **Flag major issues** - Missing components, wrong layout, broken UI
