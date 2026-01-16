#!/bin/bash
# Build the Android project
# Usage: ./scripts/build.sh [debug|release]

set -e

BUILD_TYPE=${1:-debug}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Find the actual Android project directory
if [ -d "$PROJECT_DIR/PhotosApp" ]; then
    APP_DIR="$PROJECT_DIR/PhotosApp"
elif [ -f "$PROJECT_DIR/gradlew" ]; then
    APP_DIR="$PROJECT_DIR"
else
    echo "Error: Android project not found"
    echo "Expected PhotosApp/ directory or gradlew in $PROJECT_DIR"
    exit 1
fi

cd "$APP_DIR"

echo "Building $BUILD_TYPE..."
echo "Project: $APP_DIR"
echo ""

if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew assembleRelease
else
    ./gradlew assembleDebug
fi

echo ""
echo "Build complete!"

# Find and display APK location
APK_PATH=$(find "$APP_DIR" -name "*.apk" -path "*/$BUILD_TYPE/*" 2>/dev/null | head -n 1)
if [ -n "$APK_PATH" ]; then
    echo "APK: $APK_PATH"
fi
