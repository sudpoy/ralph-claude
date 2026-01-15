#!/bin/bash
# Full validation: build, install, run, and screenshot
# Usage: ./scripts/validate.sh [story_id]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
STORY_ID=${1:-"manual"}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "========================================="
echo "Full Validation for $STORY_ID"
echo "========================================="
echo ""

# Step 1: Check environment
echo "Step 1: Checking environment..."
"$SCRIPT_DIR/check-environment.sh" || {
    echo "Environment check failed. Please fix issues before validating."
    exit 1
}
echo ""

# Step 2: Build
echo "Step 2: Building..."
"$SCRIPT_DIR/build.sh" debug || {
    echo "Build failed!"
    exit 1
}
echo ""

# Step 3: Check emulator
echo "Step 3: Checking emulator..."
if ! adb devices 2>/dev/null | grep -q "emulator-\|device"; then
    echo "No emulator running. Starting one..."
    "$SCRIPT_DIR/start-emulator.sh"
fi
echo ""

# Step 4: Install and run
echo "Step 4: Installing and launching..."
"$SCRIPT_DIR/install-and-run.sh" || {
    echo "Install/run failed!"
    exit 1
}
echo ""

# Step 5: Wait for app to load
echo "Step 5: Waiting for app to load..."
sleep 3
echo ""

# Step 6: Take screenshot
echo "Step 6: Taking screenshot..."
SCREENSHOT_PATH="$PROJECT_DIR/screenshots/${STORY_ID}_${TIMESTAMP}.png"
"$SCRIPT_DIR/screenshot.sh" "$SCREENSHOT_PATH"
echo ""

echo "========================================="
echo "Validation complete!"
echo "Screenshot: $SCREENSHOT_PATH"
echo "========================================="
echo ""
echo "Review the screenshot to verify the UI matches expectations."
