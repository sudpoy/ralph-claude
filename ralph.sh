#!/bin/bash
# Ralph Wiggum - Long-running AI agent loop for Claude Code
# Usage: ./ralph.sh <project_dir> [max_iterations]
# Example: ./ralph.sh ios/photos-app 10

set -e

# Validate arguments
if [ -z "$1" ]; then
  echo "Usage: ./ralph.sh <project_dir> [max_iterations]"
  echo "Example: ./ralph.sh ios/photos-app 10"
  exit 1
fi

PROJECT_DIR="$1"
MAX_ITERATIONS=${2:-10}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Resolve project directory (support both relative and absolute paths)
if [[ "$PROJECT_DIR" = /* ]]; then
  PROJECT_PATH="$PROJECT_DIR"
else
  PROJECT_PATH="$SCRIPT_DIR/$PROJECT_DIR"
fi

# Validate project directory exists
if [ ! -d "$PROJECT_PATH" ]; then
  echo "Error: Project directory not found: $PROJECT_PATH"
  exit 1
fi

PRD_FILE="$PROJECT_PATH/prd.json"
PROGRESS_FILE="$PROJECT_PATH/progress.txt"
ARCHIVE_DIR="$PROJECT_PATH/archive"
LAST_BRANCH_FILE="$PROJECT_PATH/.last-branch"

# Validate prd.json exists
if [ ! -f "$PRD_FILE" ]; then
  echo "Error: prd.json not found in $PROJECT_PATH"
  echo "Run /ralph to convert your prd.md to prd.json first."
  exit 1
fi

# Archive previous run if branch changed
if [ -f "$PRD_FILE" ] && [ -f "$LAST_BRANCH_FILE" ]; then
  CURRENT_BRANCH=$(jq -r '.branchName // empty' "$PRD_FILE" 2>/dev/null || echo "")
  LAST_BRANCH=$(cat "$LAST_BRANCH_FILE" 2>/dev/null || echo "")

  if [ -n "$CURRENT_BRANCH" ] && [ -n "$LAST_BRANCH" ] && [ "$CURRENT_BRANCH" != "$LAST_BRANCH" ]; then
    # Archive the previous run
    DATE=$(date +%Y-%m-%d)
    # Strip "ralph/" prefix from branch name for folder
    FOLDER_NAME=$(echo "$LAST_BRANCH" | sed 's|^ralph/||')
    ARCHIVE_FOLDER="$ARCHIVE_DIR/$DATE-$FOLDER_NAME"

    echo "Archiving previous run: $LAST_BRANCH"
    mkdir -p "$ARCHIVE_FOLDER"
    [ -f "$PRD_FILE" ] && cp "$PRD_FILE" "$ARCHIVE_FOLDER/"
    [ -f "$PROGRESS_FILE" ] && cp "$PROGRESS_FILE" "$ARCHIVE_FOLDER/"
    echo "   Archived to: $ARCHIVE_FOLDER"

    # Reset progress file for new run
    echo "# Ralph Progress Log" > "$PROGRESS_FILE"
    echo "Project: $PROJECT_DIR" >> "$PROGRESS_FILE"
    echo "Started: $(date)" >> "$PROGRESS_FILE"
    echo "---" >> "$PROGRESS_FILE"
  fi
fi

# Track current branch
if [ -f "$PRD_FILE" ]; then
  CURRENT_BRANCH=$(jq -r '.branchName // empty' "$PRD_FILE" 2>/dev/null || echo "")
  if [ -n "$CURRENT_BRANCH" ]; then
    echo "$CURRENT_BRANCH" > "$LAST_BRANCH_FILE"
  fi
fi

# Initialize progress file if it doesn't exist
if [ ! -f "$PROGRESS_FILE" ]; then
  echo "# Ralph Progress Log" > "$PROGRESS_FILE"
  echo "Project: $PROJECT_DIR" >> "$PROGRESS_FILE"
  echo "Started: $(date)" >> "$PROGRESS_FILE"
  echo "---" >> "$PROGRESS_FILE"
fi

echo "Starting Ralph"
echo "  Project: $PROJECT_DIR"
echo "  Max iterations: $MAX_ITERATIONS"

for i in $(seq 1 $MAX_ITERATIONS); do
  echo ""
  echo "═══════════════════════════════════════════════════════"
  echo "  Ralph Iteration $i of $MAX_ITERATIONS"
  echo "  Project: $PROJECT_DIR"
  echo "═══════════════════════════════════════════════════════"

  # Create a temporary prompt with project-specific paths
  TEMP_PROMPT=$(mktemp)
  sed "s|prd.json|$PRD_FILE|g; s|progress.txt|$PROGRESS_FILE|g" "$SCRIPT_DIR/prompt.md" > "$TEMP_PROMPT"

  # Run claude with the ralph prompt
  OUTPUT=$(cat "$TEMP_PROMPT" | claude --dangerously-skip-permissions 2>&1 | tee /dev/stderr) || true

  # Clean up temp file
  rm -f "$TEMP_PROMPT"

  # Check for completion signal
  if echo "$OUTPUT" | grep -q "<promise>COMPLETE</promise>"; then
    echo ""
    echo "Ralph completed all tasks!"
    echo "Completed at iteration $i of $MAX_ITERATIONS"
    exit 0
  fi

  echo "Iteration $i complete. Continuing..."
  sleep 2
done

echo ""
echo "Ralph reached max iterations ($MAX_ITERATIONS) without completing all tasks."
echo "Check $PROGRESS_FILE for status."
exit 1
