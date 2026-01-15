# Android Development Patterns

## Overview

This directory contains Android projects managed by Ralph.

## Environment Setup (REQUIRED FIRST)

Before any development work, ensure Android development tools are installed:

1. **Install Android Studio** from https://developer.android.com/studio
   - This includes Java, SDK, Emulator, and ADB

2. **Create an AVD (Android Virtual Device)**
   - Open Android Studio
   - Tools > Device Manager > Create Device
   - Select "Pixel 7" or similar, API 34

3. **Set environment variables** (add to `~/.zshrc`):
   ```bash
   export ANDROID_HOME=$HOME/Library/Android/sdk
   export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools
   ```

4. **Verify setup**:
   ```bash
   ./scripts/check-environment.sh
   ```

## Validation Scripts

Each project has validation scripts in `<project>/scripts/`:

| Script | Purpose |
|--------|---------|
| `check-environment.sh` | Verify all dev tools are installed |
| `build.sh` | Build the project (debug or release) |
| `start-emulator.sh` | Start Android emulator |
| `install-and-run.sh` | Build, install, and launch app |
| `screenshot.sh` | Capture screenshot from emulator |
| `validate.sh` | Full validation: build, install, run, screenshot |
| `ui-actions.sh` | ADB UI helpers (tap, scroll, type) - source this |

### Usage Examples

```bash
# Check environment is ready
./scripts/check-environment.sh

# Build debug APK
./scripts/build.sh debug

# Start emulator (uses first available AVD)
./scripts/start-emulator.sh

# Build, install, and run app
./scripts/install-and-run.sh

# Take screenshot for validation
./scripts/screenshot.sh /path/to/output.png

# Full validation for a story
./scripts/validate.sh US-006
```

## Build & Validation Commands

```bash
# Build debug APK (inside project directory)
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run lint checks
./gradlew lint

# Run all checks
./gradlew check

# Clean build
./gradlew clean assembleDebug
```

## Emulator Commands

```bash
# List available AVDs
emulator -list-avds

# Start emulator
emulator -avd <AVD_NAME>

# List connected devices
adb devices

# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n <package>/<activity>

# Take screenshot
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png ./screenshot.png

# View logs
adb logcat | grep -i "photosapp"

# Clear app data
adb shell pm clear <package>
```

## Patterns

- Use Kotlin for all new code
- Follow MVVM architecture pattern
- Use Jetpack Compose for UI
- Use Hilt for dependency injection
- Use Coroutines and Flow for async operations
- Use Coil for image loading

## Project Structure (Clean Architecture)

```
app/src/main/java/com/example/appname/
├── data/                    # Data layer
│   ├── datasource/          # Data sources (MediaStore, API, etc.)
│   ├── repository/          # Repository implementations
│   └── di/                   # Hilt modules for data layer
├── domain/                  # Domain layer
│   ├── model/               # Domain models
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Use cases
└── presentation/            # Presentation layer
    ├── components/          # Reusable Composables
    ├── screens/             # Screen Composables
    ├── theme/               # Material theme
    └── viewmodel/           # ViewModels
```

## Testing

- Unit tests in `src/test/`
- Instrumented tests in `src/androidTest/`
- UI tests use Compose testing APIs

## UI Validation (MANDATORY for UI Stories)

For any story that changes UI, you MUST validate using screenshots and AI vision comparison.

### Validation Workflow

1. **Run validation script:**
   ```bash
   ./scripts/validate.sh <STORY_ID>
   ```

2. **Read the captured screenshot:**
   ```
   Read <project>/screenshots/<STORY_ID>/<STORY_ID>_main_<timestamp>.png
   ```

3. **Read the mock reference:**
   ```
   Read <project>/mocks/main.PNG
   ```

4. **Compare using AI vision** - Verify:
   - Layout matches mock structure
   - All required UI elements are present
   - Colors and spacing are correct
   - Interactive elements are visible
   - No visual glitches

5. **Only mark `passes: true` if ALL criteria are met**

### UI Navigation with ADB

For complex validations, use the UI action helpers:

```bash
# Source the helpers first
source ./scripts/ui-actions.sh

# Tap actions
tap 500 300                    # Tap at coordinates
tap_percent 50 50              # Tap at screen percentage (center)
long_press 500 300 1000        # Long press (1 second)
double_tap 500 300             # Double tap

# Scroll actions
scroll_down                    # Scroll down 30%
scroll_up                      # Scroll up 30%
scroll_left                    # Horizontal scroll left
scroll_right                   # Horizontal scroll right

# Navigation
tap_bottom_nav 0               # Tap first bottom nav item
tap_bottom_nav 1               # Tap second item, etc.
press_back                     # Press back button
press_home                     # Press home button

# Text input
type_text "hello world"        # Type text
clear_text                     # Clear text field

# App control
launch_app <package> <activity>
stop_app <package>
clear_app_data <package>
grant_permission               # Grant permission dialog

# Screenshots
take_screenshot /path/to.png   # Capture screenshot

# Wait
wait_for_ui 3                  # Wait 3 seconds
wait_for_app <package> 10      # Wait for app (10s timeout)
```

### Screenshot Comparison Criteria

When comparing screenshots to mocks, check:

| Aspect | What to Verify |
|--------|----------------|
| **Layout** | Elements in correct positions (top bar, content, bottom nav) |
| **Presence** | All required UI components visible |
| **Styling** | Colors approximately match, proper spacing |
| **Content** | Images load (not blank), text is correct |
| **State** | Correct selection states (tabs, buttons) |
| **Consistency** | Overall appearance matches mock |

### Validation Failure

If validation fails:
- Do NOT mark `passes: true`
- Document specific failures in progress.txt
- Note which acceptance criteria failed
- Describe screenshot vs. expected
- Next iteration will fix the issue

### Example Validation Output

```markdown
## Validation: US-006 - Bottom Navigation

Screenshots compared:
- screenshots/US-006/US-006_main.png
- mocks/main.PNG

Verification:
✅ Navigation bar visible at bottom
✅ 4 tabs present
✅ Photos tab selected (filled icon)
❌ Ask icon uses wrong icon (star vs sparkle)

Result: FAIL - Need to fix Ask icon
```

See `<project>/VALIDATION_GUIDE.md` for detailed story-specific validation instructions.

## Common Gotchas

- Always update `AndroidManifest.xml` when adding new activities
- Remember to add permissions for camera, storage, internet
- Use `@AndroidEntryPoint` on Activities/Fragments for Hilt
- Use `@HiltViewModel` on ViewModels
- ProGuard rules may need updates when adding new libraries
- MediaStore queries require READ_MEDIA_IMAGES (API 33+) or READ_EXTERNAL_STORAGE
- **UI stories require emulator validation** - never mark passes without screenshots
