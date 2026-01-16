#!/bin/bash
# Check Android development environment prerequisites
# Run this before starting development to ensure all tools are available

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================="
echo "Android Development Environment Check"
echo "========================================="
echo ""

ERRORS=0

# Check Java
echo -n "Java 17+: "
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ] 2>/dev/null; then
        echo -e "${GREEN}✓ Found (version $JAVA_VERSION)${NC}"
    else
        echo -e "${RED}✗ Found but version too old ($JAVA_VERSION, need 17+)${NC}"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo -e "${RED}✗ Not found${NC}"
    echo "  Install: brew install openjdk@17"
    ERRORS=$((ERRORS + 1))
fi

# Check ANDROID_HOME
echo -n "ANDROID_HOME: "
if [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
    echo -e "${GREEN}✓ Set ($ANDROID_HOME)${NC}"
elif [ -d "$HOME/Library/Android/sdk" ]; then
    echo -e "${YELLOW}⚠ Not set, but SDK found at ~/Library/Android/sdk${NC}"
    echo "  Add to ~/.zshrc: export ANDROID_HOME=\$HOME/Library/Android/sdk"
    echo "                   export PATH=\$PATH:\$ANDROID_HOME/emulator:\$ANDROID_HOME/platform-tools"
elif [ -d "/usr/local/share/android-commandlinetools" ]; then
    echo -e "${YELLOW}⚠ Not set, but SDK found at /usr/local/share/android-commandlinetools${NC}"
else
    echo -e "${RED}✗ Not set and SDK not found${NC}"
    echo "  Install Android Studio or run: brew install --cask android-commandlinetools"
    ERRORS=$((ERRORS + 1))
fi

# Check ADB
echo -n "ADB: "
if command -v adb &> /dev/null; then
    ADB_VERSION=$(adb version | head -n 1)
    echo -e "${GREEN}✓ Found ($ADB_VERSION)${NC}"
else
    echo -e "${RED}✗ Not found${NC}"
    echo "  Ensure \$ANDROID_HOME/platform-tools is in PATH"
    ERRORS=$((ERRORS + 1))
fi

# Check Emulator
echo -n "Emulator: "
if command -v emulator &> /dev/null; then
    echo -e "${GREEN}✓ Found${NC}"
else
    echo -e "${RED}✗ Not found${NC}"
    echo "  Ensure \$ANDROID_HOME/emulator is in PATH"
    ERRORS=$((ERRORS + 1))
fi

# Check AVDs
echo -n "AVDs: "
if command -v emulator &> /dev/null; then
    AVDS=$(emulator -list-avds 2>/dev/null | wc -l | tr -d ' ')
    if [ "$AVDS" -gt 0 ]; then
        echo -e "${GREEN}✓ Found $AVDS AVD(s)${NC}"
        emulator -list-avds 2>/dev/null | sed 's/^/    /'
    else
        echo -e "${YELLOW}⚠ No AVDs configured${NC}"
        echo "  Create one in Android Studio: Tools > Device Manager > Create Device"
    fi
else
    echo -e "${RED}✗ Cannot check (emulator not found)${NC}"
fi

# Check Gradle wrapper
echo -n "Gradle wrapper: "
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
if [ -f "$PROJECT_DIR/PhotosApp/gradlew" ]; then
    echo -e "${GREEN}✓ Found${NC}"
elif [ -f "$PROJECT_DIR/gradlew" ]; then
    echo -e "${GREEN}✓ Found${NC}"
else
    echo -e "${YELLOW}⚠ Not found (will be created when project is set up)${NC}"
fi

echo ""
echo "========================================="
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}Environment ready for Android development!${NC}"
    exit 0
else
    echo -e "${RED}$ERRORS issue(s) found. Please fix before proceeding.${NC}"
    exit 1
fi
