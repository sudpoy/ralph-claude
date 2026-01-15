#!/bin/bash
# Full validation: build, install, run, navigate, screenshot, and prepare for AI comparison
# Usage: ./scripts/validate.sh <story_id> [validation_script]
#
# Examples:
#   ./scripts/validate.sh US-006                    # Basic validation
#   ./scripts/validate.sh US-006 validate-us006.sh # With custom navigation script

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
STORY_ID=${1:-"manual"}
VALIDATION_SCRIPT=${2:-""}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
SCREENSHOTS_DIR="$PROJECT_DIR/screenshots/$STORY_ID"
MOCK_DIR="$PROJECT_DIR/mocks"

# Source UI actions
source "$SCRIPT_DIR/ui-actions.sh"

echo "========================================="
echo "Full Validation for $STORY_ID"
echo "========================================="
echo ""

# Create screenshots directory
mkdir -p "$SCREENSHOTS_DIR"

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

# Step 5: Wait for app and grant permissions if needed
echo "Step 5: Waiting for app to load..."
wait_for_ui 3
# Try to grant permission if dialog appears
grant_permission 2>/dev/null || true
wait_for_ui 2
echo ""

# Step 6: Run custom validation script if provided
if [ -n "$VALIDATION_SCRIPT" ] && [ -f "$SCRIPT_DIR/$VALIDATION_SCRIPT" ]; then
    echo "Step 6: Running custom validation script..."
    source "$SCRIPT_DIR/$VALIDATION_SCRIPT"
    echo ""
else
    echo "Step 6: No custom validation script. Taking initial screenshot..."
fi

# Step 7: Take screenshots
echo "Step 7: Capturing screenshots..."

# Main screenshot
MAIN_SCREENSHOT="$SCREENSHOTS_DIR/${STORY_ID}_main_${TIMESTAMP}.png"
take_screenshot "$MAIN_SCREENSHOT"

# Scroll down and take another
scroll_down
wait_for_ui 1
SCROLL_SCREENSHOT="$SCREENSHOTS_DIR/${STORY_ID}_scrolled_${TIMESTAMP}.png"
take_screenshot "$SCROLL_SCREENSHOT"

echo ""

# Step 8: Generate validation report
REPORT_FILE="$SCREENSHOTS_DIR/validation_report.md"
echo "Step 8: Generating validation report..."

cat > "$REPORT_FILE" << EOF
# Validation Report: $STORY_ID

**Timestamp:** $(date)
**Project:** PhotosApp

## Screenshots Captured

### Main Screen
![Main Screenshot](./${STORY_ID}_main_${TIMESTAMP}.png)

### After Scroll
![Scrolled Screenshot](./${STORY_ID}_scrolled_${TIMESTAMP}.png)

## Mock Reference
Reference mock is located at: \`$MOCK_DIR/main.PNG\`

## AI Validation Instructions

To validate this story, compare the screenshots above with the mock and verify:

### For $STORY_ID, check:
$(get_validation_criteria "$STORY_ID")

## Validation Checklist

- [ ] UI elements match mock design
- [ ] Colors and spacing are correct
- [ ] Text is readable and properly aligned
- [ ] Interactive elements are visible
- [ ] No visual glitches or rendering issues

## How to Compare

1. Read the screenshot files using the Read tool
2. Read the mock file at \`$MOCK_DIR/main.PNG\`
3. Compare visually and note any differences
4. Mark story as \`passes: true\` only if all criteria are met

EOF

echo "Report saved: $REPORT_FILE"
echo ""

echo "========================================="
echo "Validation Screenshots Complete!"
echo "========================================="
echo ""
echo "Screenshots saved to: $SCREENSHOTS_DIR"
echo ""
echo "Files:"
ls -la "$SCREENSHOTS_DIR"
echo ""
echo "========================================="
echo "NEXT STEPS FOR AI VALIDATION"
echo "========================================="
echo ""
echo "1. Read the main screenshot:"
echo "   Read $MAIN_SCREENSHOT"
echo ""
echo "2. Read the mock reference:"
echo "   Read $MOCK_DIR/main.PNG"
echo ""
echo "3. Compare and verify the acceptance criteria for $STORY_ID"
echo ""
echo "4. If validation passes, update prd.json:"
echo "   Set passes: true for $STORY_ID"
echo ""

# Helper function to get validation criteria based on story ID
get_validation_criteria() {
    local story=$1
    case $story in
        "US-006")
            echo "- Bottom navigation bar is visible"
            echo "- 4 tabs present: Photos, Collections, Create, Ask"
            echo "- Photos tab is selected (filled icon)"
            echo "- Other tabs show outlined icons"
            ;;
        "US-007")
            echo "- Top bar is visible"
            echo "- 'Backup complete' chip with cloud icon on left"
            echo "- Add (+) button visible"
            echo "- Notification bell icon visible"
            echo "- Profile avatar (circular) on right"
            ;;
        "US-008")
            echo "- Memories section visible at top"
            echo "- Horizontally scrollable cards"
            echo "- Cards have rounded corners"
            echo "- Text overlay visible on cards (month/year)"
            ;;
        "US-009")
            echo "- Month header visible (e.g., 'January')"
            echo "- Checkmark icon on right"
            echo "- Three-dot menu on right"
            ;;
        "US-010")
            echo "- Photo grid is visible"
            echo "- 4 columns of thumbnails"
            echo "- Small gaps between photos"
            echo "- Photos load (not blank/placeholder)"
            ;;
        "US-011")
            echo "- Video thumbnails show play icon"
            echo "- Duration text visible on videos"
            echo "- Favorite indicator visible where applicable"
            ;;
        "US-012")
            echo "- Complete screen matches mock layout"
            echo "- TopBar at top"
            echo "- Memories section below top bar"
            echo "- Month headers with photo grids"
            echo "- Bottom navigation at bottom"
            ;;
        "US-013")
            echo "- Loading state shows spinner (if applicable)"
            echo "- Empty state message (if no photos)"
            echo "- Error state with retry button (if error)"
            ;;
        *)
            echo "- Verify UI matches acceptance criteria in PRD"
            echo "- Check for visual consistency with mock"
            ;;
    esac
}
