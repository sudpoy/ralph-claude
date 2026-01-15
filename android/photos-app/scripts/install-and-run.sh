#!/bin/bash
# Build, install, and run the app on emulator
# Usage: ./scripts/install-and-run.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
PACKAGE_NAME="com.example.photosapp"
MAIN_ACTIVITY="com.example.photosapp.MainActivity"

# Find the actual Android project directory
if [ -d "$PROJECT_DIR/PhotosApp" ]; then
    APP_DIR="$PROJECT_DIR/PhotosApp"
elif [ -f "$PROJECT_DIR/gradlew" ]; then
    APP_DIR="$PROJECT_DIR"
else
    echo "Error: Android project not found"
    exit 1
fi

# Check if emulator is running
if ! adb devices 2>/dev/null | grep -q "emulator-\|device"; then
    echo "Error: No device/emulator connected"
    echo "Run: ./scripts/start-emulator.sh"
    exit 1
fi

echo "========================================="
echo "Building and installing PhotosApp"
echo "========================================="
echo ""

cd "$APP_DIR"

# Build
echo "Step 1: Building debug APK..."
./gradlew assembleDebug

# Find APK
APK_PATH=$(find "$APP_DIR" -name "*.apk" -path "*/debug/*" 2>/dev/null | head -n 1)
if [ -z "$APK_PATH" ]; then
    echo "Error: APK not found after build"
    exit 1
fi
echo "APK: $APK_PATH"
echo ""

# Install
echo "Step 2: Installing on device..."
adb install -r "$APK_PATH"
echo ""

# Launch
echo "Step 3: Launching app..."
adb shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY"
echo ""

echo "========================================="
echo "App launched successfully!"
echo "========================================="
