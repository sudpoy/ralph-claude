# Ralph Agent Instructions

## Overview

Ralph is an autonomous AI agent loop that runs Claude Code repeatedly until all PRD items are complete. Each iteration is a fresh Claude instance with clean context.

This repository supports multiple projects across Android, iOS, and Web platforms.

## Project Structure

```
ralph-claude/
├── android/           # Android projects
│   ├── AGENTS.md      # Android-specific patterns
│   └── <project>/     # Individual projects
├── ios/               # iOS projects
│   ├── AGENTS.md      # iOS-specific patterns
│   └── <project>/     # Individual projects
└── web/               # Web projects
    ├── AGENTS.md      # Web-specific patterns
    └── <project>/     # Individual projects
```

## Commands

```bash
# Run Ralph for a specific project
./ralph.sh <platform>/<project> [max_iterations]

# Examples:
./ralph.sh ios/photos-app 10
./ralph.sh android/shopping-app 15
./ralph.sh web/dashboard 5

# Run the flowchart dev server
cd flowchart && npm run dev
```

## Key Files

### Shared (Root Level)
- `ralph.sh` - The bash loop that spawns fresh Claude Code instances
- `prompt.md` - Instructions given to each Claude instance
- `commands/prd.md` - Slash command for generating PRDs (`/prd`)
- `commands/ralph.md` - Slash command for converting PRDs to JSON (`/ralph`)
- `flowchart/` - Interactive React Flow diagram explaining how Ralph works

### Platform-Specific
- `android/AGENTS.md` - Android development patterns and commands
- `ios/AGENTS.md` - iOS development patterns and commands
- `web/AGENTS.md` - Web development patterns and commands

### Per-Project
- `prd.md` - Human-readable PRD document
- `prd.json` - Machine-readable format for Ralph
- `progress.txt` - Append-only learnings log

## Patterns

- Each iteration spawns a fresh Claude Code instance with clean context
- Memory persists via git history, `progress.txt`, and `prd.json`
- Stories should be small enough to complete in one context window
- Always update AGENTS.md with discovered patterns for future iterations
- Platform-specific patterns go in `<platform>/AGENTS.md`
- Project-specific learnings go in `<project>/progress.txt`
