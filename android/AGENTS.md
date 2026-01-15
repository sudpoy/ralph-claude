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

## Common Gotchas

- Always update `AndroidManifest.xml` when adding new activities
- Remember to add permissions for camera, storage, internet
- Use `@AndroidEntryPoint` on Activities/Fragments for Hilt
- Use `@HiltViewModel` on ViewModels
- ProGuard rules may need updates when adding new libraries
- MediaStore queries require READ_MEDIA_IMAGES (API 33+) or READ_EXTERNAL_STORAGE
