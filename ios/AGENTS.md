# iOS Development Patterns

## Overview

This directory contains iOS projects managed by Ralph.

## Build & Validation

```bash
# Build for iOS Simulator
xcodebuild -scheme <SchemeName> -destination 'generic/platform=iOS Simulator' -configuration Debug build

# Run tests
xcodebuild -scheme <SchemeName> -destination 'platform=iOS Simulator,name=iPhone 15 Pro' test

# If no simulators available
xcodebuild -scheme <SchemeName> -sdk iphonesimulator -configuration Debug build
```

## Patterns

- Use Swift for all new code
- Follow MVVM or MVC architecture as per project
- Use SwiftUI for UI (unless project specifies UIKit)
- Use Swift Concurrency (async/await) for async operations
- Use Combine for reactive patterns

## Testing

- Unit tests use XCTest framework
- UI tests use XCUITest
- Snapshot tests where applicable

## Simulator Validation

For UI stories, validate using simulator:

```bash
# Boot simulator
xcrun simctl boot "iPhone 15 Pro"

# Install app
xcrun simctl install booted /path/to/App.app

# Launch app
xcrun simctl launch booted com.example.bundleid

# Take screenshot
xcrun simctl io booted screenshot /tmp/screenshot.png
```

## Common Gotchas

- Always use `IF NOT EXISTS` for Core Data migrations
- Update Info.plist for new permissions
- SwiftUI previews may need `#Preview` macro in Xcode 15+
