#!/bin/bash
# Download sample photos and push to emulator for testing
# Uses Lorem Picsum (free placeholder image service)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SAMPLE_DIR="$PROJECT_DIR/sample-photos"
COUNT=${1:-100}

echo "========================================="
echo "Loading $COUNT sample photos to emulator"
echo "========================================="

# Check for connected device
if ! adb devices 2>/dev/null | grep -q "device$"; then
    echo "Error: No device/emulator connected"
    exit 1
fi

# Create local sample directory
mkdir -p "$SAMPLE_DIR"

echo ""
echo "Step 1: Downloading $COUNT sample images..."
echo "(Using Lorem Picsum - free placeholder images)"
echo ""

for i in $(seq 1 $COUNT); do
    # Random dimensions between 800-1200
    WIDTH=$((800 + RANDOM % 400))
    HEIGHT=$((800 + RANDOM % 400))

    OUTPUT_FILE="$SAMPLE_DIR/photo_$(printf "%03d" $i).jpg"

    if [ ! -f "$OUTPUT_FILE" ]; then
        # Lorem Picsum provides random free images
        curl -sL "https://picsum.photos/$WIDTH/$HEIGHT" -o "$OUTPUT_FILE"
        echo "Downloaded $i/$COUNT: photo_$(printf "%03d" $i).jpg (${WIDTH}x${HEIGHT})"
    else
        echo "Skipped $i/$COUNT: already exists"
    fi

    # Small delay to be nice to the server
    sleep 0.2
done

echo ""
echo "Step 2: Creating directory on device..."
adb shell mkdir -p /sdcard/DCIM/SamplePhotos

echo ""
echo "Step 3: Pushing photos to device..."
adb push "$SAMPLE_DIR/." /sdcard/DCIM/SamplePhotos/

echo ""
echo "Step 4: Scanning media to update MediaStore..."
adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/DCIM/SamplePhotos

# Force full media scan
adb shell "find /sdcard/DCIM/SamplePhotos -name '*.jpg' -exec am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://{} \;"

echo ""
echo "========================================="
echo "Done! $COUNT sample photos loaded."
echo "========================================="
echo ""
echo "Photos saved locally: $SAMPLE_DIR"
echo "Photos on device: /sdcard/DCIM/SamplePhotos"
echo ""
echo "To remove from device later:"
echo "  adb shell rm -rf /sdcard/DCIM/SamplePhotos"
echo ""
echo "To remove local copies:"
echo "  rm -rf $SAMPLE_DIR"
echo ""
