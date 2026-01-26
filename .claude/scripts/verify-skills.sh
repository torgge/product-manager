#!/bin/bash
# Verify agent skill structure is intact

SKILLS_DIR=".claude/skills"
REQUIRED_SKILLS=(
    "dev"
    "build"
    "test"
    "db"
    "gen-entity"
    "gen-service"
    "gen-controller"
    "gen-resource"
    "gen-template"
    "gen-crud"
    "conventions"
    "architecture"
)

missing=()

for skill in "${REQUIRED_SKILLS[@]}"; do
    if [[ ! -f "$SKILLS_DIR/$skill/SKILL.md" ]]; then
        missing+=("$skill")
    fi
done

if [[ ${#missing[@]} -gt 0 ]]; then
    echo "WARNING: Missing skills: ${missing[*]}"
    echo "Run skill regeneration or check .claude/skills/ directory"
    exit 1
fi

echo "All ${#REQUIRED_SKILLS[@]} skills verified"
exit 0
