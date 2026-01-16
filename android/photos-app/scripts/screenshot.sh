#!/bin/bash
# Take a screenshot from the emulator
# Usage: ./scripts/screenshot.sh [output_path]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
TIMESTAMP=$(date +date_%m_%d_time_%H_%M)
OUTPUT_PATH=${1:-"$PROJECT_DIR/screenshots/screenshot_$TIMESTAMP.png"}

# Create screenshots directory if needed
mkdir -p "$(dirname "$OUTPUT_PATH")"

# Check if device is connected
if ! adb devices 2>/dev/null | grep -q "emulator-\|device"; then
    echo "Error: No device/emulator connected"
    exit 1
fi

echo "Taking screenshot..."

# Take screenshot and pull to local machine
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png "$OUTPUT_PATH"
adb shell rm /sdcard/screenshot.png

echo "Screenshot saved: $OUTPUT_PATH"

# On macOS, optionally open the screenshot
if [ "$(uname)" = "Darwin" ]; then
    open "$OUTPUT_PATH" 2>/dev/null || true
fi
