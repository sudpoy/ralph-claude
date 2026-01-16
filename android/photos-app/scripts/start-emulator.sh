#!/bin/bash
# Start Android emulator
# Usage: ./scripts/start-emulator.sh [avd_name]

set -e

# Get AVD name from argument or use first available
if [ -n "$1" ]; then
    AVD_NAME="$1"
else
    AVD_NAME=$(emulator -list-avds 2>/dev/null | head -n 1)
fi

if [ -z "$AVD_NAME" ]; then
    echo "Error: No AVD found"
    echo ""
    echo "Create an AVD using Android Studio:"
    echo "  Tools > Device Manager > Create Device"
    echo ""
    echo "Or via command line:"
    echo "  sdkmanager 'system-images;android-34;google_apis;arm64-v8a'"
    echo "  avdmanager create avd -n Pixel_7_API_34 -k 'system-images;android-34;google_apis;arm64-v8a' -d pixel_7"
    exit 1
fi

# Check if emulator is already running
if adb devices 2>/dev/null | grep -q "emulator-"; then
    echo "Emulator already running"
    adb devices
    exit 0
fi

echo "Starting emulator: $AVD_NAME"
echo ""

# Start emulator in background
emulator -avd "$AVD_NAME" -no-snapshot-load &

# Wait for emulator to boot
echo "Waiting for emulator to boot..."
adb wait-for-device

# Wait for boot animation to complete
while [ "$(adb shell getprop sys.boot_completed 2>/dev/null)" != "1" ]; do
    sleep 2
    echo -n "."
done
echo ""

echo "Emulator ready!"
adb devices
