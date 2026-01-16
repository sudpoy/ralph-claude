#!/bin/bash
# ADB UI Actions - Helper functions for UI automation
# Source this file: source ./scripts/ui-actions.sh

# ============================================
# DEVICE INFO
# ============================================

# Get screen dimensions
get_screen_size() {
    adb shell wm size | grep -oE '[0-9]+x[0-9]+'
}

# Get screen width
get_screen_width() {
    get_screen_size | cut -d'x' -f1
}

# Get screen height
get_screen_height() {
    get_screen_size | cut -d'x' -f2
}

# ============================================
# TAP ACTIONS
# ============================================

# Tap at coordinates
# Usage: tap 500 300
tap() {
    local x=$1
    local y=$2
    echo "Tapping at ($x, $y)"
    adb shell input tap "$x" "$y"
    sleep 0.5
}

# Tap at percentage of screen
# Usage: tap_percent 50 50  (center of screen)
tap_percent() {
    local x_percent=$1
    local y_percent=$2
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local x=$((width * x_percent / 100))
    local y=$((height * y_percent / 100))
    tap "$x" "$y"
}

# Long press at coordinates
# Usage: long_press 500 300 1000  (1000ms duration)
long_press() {
    local x=$1
    local y=$2
    local duration=${3:-1000}
    echo "Long pressing at ($x, $y) for ${duration}ms"
    adb shell input swipe "$x" "$y" "$x" "$y" "$duration"
    sleep 0.5
}

# Double tap
# Usage: double_tap 500 300
double_tap() {
    local x=$1
    local y=$2
    tap "$x" "$y"
    sleep 0.1
    tap "$x" "$y"
}

# ============================================
# SWIPE/SCROLL ACTIONS
# ============================================

# Swipe from point A to point B
# Usage: swipe 500 800 500 200 300  (x1 y1 x2 y2 duration_ms)
swipe() {
    local x1=$1
    local y1=$2
    local x2=$3
    local y2=$4
    local duration=${5:-300}
    echo "Swiping from ($x1, $y1) to ($x2, $y2)"
    adb shell input swipe "$x1" "$y1" "$x2" "$y2" "$duration"
    sleep 0.5
}

# Scroll down
# Usage: scroll_down [distance_percent]
scroll_down() {
    local distance=${1:-30}
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local x=$((width / 2))
    local y1=$((height * 70 / 100))
    local y2=$((height * (70 - distance) / 100))
    swipe "$x" "$y1" "$x" "$y2"
}

# Scroll up
# Usage: scroll_up [distance_percent]
scroll_up() {
    local distance=${1:-30}
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local x=$((width / 2))
    local y1=$((height * 30 / 100))
    local y2=$((height * (30 + distance) / 100))
    swipe "$x" "$y1" "$x" "$y2"
}

# Scroll left (horizontal)
scroll_left() {
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local y=$((height / 2))
    local x1=$((width * 80 / 100))
    local x2=$((width * 20 / 100))
    swipe "$x1" "$y" "$x2" "$y"
}

# Scroll right (horizontal)
scroll_right() {
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local y=$((height / 2))
    local x1=$((width * 20 / 100))
    local x2=$((width * 80 / 100))
    swipe "$x1" "$y" "$x2" "$y"
}

# ============================================
# TEXT INPUT
# ============================================

# Type text
# Usage: type_text "hello world"
type_text() {
    local text="$1"
    # Replace spaces with %s for adb
    local escaped=$(echo "$text" | sed 's/ /%s/g')
    echo "Typing: $text"
    adb shell input text "$escaped"
    sleep 0.3
}

# Clear text field (select all + delete)
clear_text() {
    # Ctrl+A to select all
    adb shell input keyevent KEYCODE_CTRL_A
    sleep 0.1
    # Delete
    adb shell input keyevent KEYCODE_DEL
    sleep 0.3
}

# ============================================
# BUTTON/KEY EVENTS
# ============================================

# Press back button
press_back() {
    echo "Pressing BACK"
    adb shell input keyevent KEYCODE_BACK
    sleep 0.5
}

# Press home button
press_home() {
    echo "Pressing HOME"
    adb shell input keyevent KEYCODE_HOME
    sleep 0.5
}

# Press enter
press_enter() {
    echo "Pressing ENTER"
    adb shell input keyevent KEYCODE_ENTER
    sleep 0.3
}

# Press tab
press_tab() {
    adb shell input keyevent KEYCODE_TAB
    sleep 0.2
}

# Open recent apps
open_recents() {
    adb shell input keyevent KEYCODE_APP_SWITCH
    sleep 0.5
}

# ============================================
# APP CONTROL
# ============================================

# Launch app by package/activity
# Usage: launch_app com.example.app com.example.app.MainActivity
launch_app() {
    local package=$1
    local activity=$2
    echo "Launching $package/$activity"
    adb shell am start -n "$package/$activity"
    sleep 2
}

# Force stop app
# Usage: stop_app com.example.app
stop_app() {
    local package=$1
    echo "Stopping $package"
    adb shell am force-stop "$package"
    sleep 0.5
}

# Clear app data
# Usage: clear_app_data com.example.app
clear_app_data() {
    local package=$1
    echo "Clearing data for $package"
    adb shell pm clear "$package"
    sleep 0.5
}

# Check if app is in foreground
# Usage: is_app_foreground com.example.app
is_app_foreground() {
    local package=$1
    adb shell dumpsys activity activities | grep -q "mResumedActivity.*$package"
}

# ============================================
# SCREENSHOTS
# ============================================

# Take screenshot and pull to local
# Usage: take_screenshot /path/to/output.png
take_screenshot() {
    local output=${1:-"/tmp/screenshot.png"}
    echo "Taking screenshot -> $output"
    adb shell screencap -p /sdcard/screenshot_temp.png
    adb pull /sdcard/screenshot_temp.png "$output" 2>/dev/null
    adb shell rm /sdcard/screenshot_temp.png
    echo "Screenshot saved: $output"
}

# ============================================
# WAIT HELPERS
# ============================================

# Wait for app to be in foreground
# Usage: wait_for_app com.example.app 10  (timeout in seconds)
wait_for_app() {
    local package=$1
    local timeout=${2:-10}
    local count=0
    echo "Waiting for $package to be in foreground..."
    while ! is_app_foreground "$package"; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $timeout ]; then
            echo "Timeout waiting for app"
            return 1
        fi
    done
    echo "App is in foreground"
    return 0
}

# Wait for UI to settle
wait_for_ui() {
    local seconds=${1:-2}
    echo "Waiting ${seconds}s for UI to settle..."
    sleep "$seconds"
}

# ============================================
# UI INSPECTION (requires uiautomator)
# ============================================

# Dump UI hierarchy to file
dump_ui() {
    local output=${1:-"/tmp/ui_dump.xml"}
    adb shell uiautomator dump /sdcard/ui_dump.xml
    adb pull /sdcard/ui_dump.xml "$output" 2>/dev/null
    adb shell rm /sdcard/ui_dump.xml
    echo "UI dump saved: $output"
}

# Find element bounds by text
# Usage: find_by_text "Button Text"
# Returns: bounds like "[0,0][100,100]"
find_by_text() {
    local text="$1"
    local dump="/tmp/ui_dump_temp.xml"
    dump_ui "$dump"
    grep -oP "text=\"$text\"[^>]*bounds=\"\[[0-9,]+\]\[[0-9,]+\]\"" "$dump" | grep -oP 'bounds="\[[0-9,]+\]\[[0-9,]+\]"' | head -1
}

# Tap element by text
# Usage: tap_text "Button Text"
tap_text() {
    local text="$1"
    local bounds=$(find_by_text "$text")
    if [ -z "$bounds" ]; then
        echo "Element with text '$text' not found"
        return 1
    fi
    # Extract center coordinates from bounds
    local coords=$(echo "$bounds" | grep -oP '\[([0-9]+),([0-9]+)\]' | head -1)
    local x1=$(echo "$bounds" | grep -oP '\[([0-9]+),' | head -1 | tr -d '[,')
    local y1=$(echo "$bounds" | grep -oP ',([0-9]+)\]' | head -1 | tr -d ',]')
    local x2=$(echo "$bounds" | grep -oP '\[([0-9]+),' | tail -1 | tr -d '[,')
    local y2=$(echo "$bounds" | grep -oP ',([0-9]+)\]' | tail -1 | tr -d ',]')
    local x=$(( (x1 + x2) / 2 ))
    local y=$(( (y1 + y2) / 2 ))
    tap "$x" "$y"
}

# ============================================
# COMMON UI PATTERNS
# ============================================

# Grant permission when dialog appears
grant_permission() {
    echo "Looking for permission dialog..."
    sleep 1
    # Try to tap "Allow" or "While using the app"
    tap_text "Allow" 2>/dev/null || tap_text "While using the app" 2>/dev/null || tap_text "ALLOW" 2>/dev/null
}

# Dismiss dialog by tapping outside or pressing back
dismiss_dialog() {
    press_back
}

# Tap bottom navigation item by index (0-based, left to right)
# Usage: tap_bottom_nav 0  (first tab)
tap_bottom_nav() {
    local index=$1
    local width=$(get_screen_width)
    local height=$(get_screen_height)
    local nav_items=4  # Adjust based on your app
    local item_width=$((width / nav_items))
    local x=$((item_width * index + item_width / 2))
    local y=$((height - 50))  # Near bottom
    tap "$x" "$y"
}

echo "UI Actions loaded. Available functions:"
echo "  tap, tap_percent, long_press, double_tap"
echo "  swipe, scroll_down, scroll_up, scroll_left, scroll_right"
echo "  type_text, clear_text"
echo "  press_back, press_home, press_enter"
echo "  launch_app, stop_app, clear_app_data"
echo "  take_screenshot, dump_ui"
echo "  tap_text, find_by_text, grant_permission"
echo "  tap_bottom_nav, wait_for_app, wait_for_ui"
