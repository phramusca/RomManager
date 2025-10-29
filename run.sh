#!/bin/bash

# RomManager Launcher
# This script launches RomManager with JavaFX support

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 Starting RomManager..."
echo ""

# Build the project first
echo "Building project..."
if ! mvn clean package -q; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"
echo ""

# Create target/lib directory if it doesn't exist
mkdir -p target/lib

# Copy all dependencies
echo "Copying dependencies..."
mvn dependency:copy-dependencies -DoutputDirectory=target/lib -q

echo "Starting RomManager with JavaFX support..."
java --module-path target/lib \
     --add-modules javafx.controls,javafx.media,javafx.swing \
     -cp "target/classes:target/lib/*" \
     rommanager.main.RomManager

echo ""
echo "RomManager closed."
