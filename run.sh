#!/bin/bash

# RomManager Launcher with JavaFX support
# Direct execution with separated classpaths (Method 2 only)

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "🚀 Starting RomManager with JavaFX support..."
echo ""

# Build the project first
echo "Building project..."
if ! mvn clean package -q; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✅ Build successful!"

# Ensure target/lib exists and copy dependencies
echo "Copying dependencies..."
mkdir -p target/lib
mvn dependency:copy-dependencies -DoutputDirectory=target/lib -q

echo "Starting RomManager with JavaFX modules and separated classpaths..."

# Create separate directories for JavaFX modules and other dependencies
JAVAFX_MODULE_PATH="target/lib/javafx"
OTHER_LIB_PATH="target/lib/other"

mkdir -p "$JAVAFX_MODULE_PATH"
mkdir -p "$OTHER_LIB_PATH"

# Copy JavaFX modules to separate directory
for jar in target/lib/*.jar; do
    if [[ "$jar" == *"javafx-"* ]]; then
        cp "$jar" "$JAVAFX_MODULE_PATH/"
    else
        cp "$jar" "$OTHER_LIB_PATH/"
    fi
done

# Define classpath for all dependencies
ALL_CLASSPATH="target/classes"
for jar in "$OTHER_LIB_PATH"/*.jar; do
    ALL_CLASSPATH="$ALL_CLASSPATH:$jar"
done

echo "JavaFX module path: $JAVAFX_MODULE_PATH"
echo "Other classpath: $ALL_CLASSPATH"

# Launch with separated modules and classpath
java --module-path "$JAVAFX_MODULE_PATH" \
     --add-modules javafx.controls,javafx.media,javafx.swing \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.io=ALL-UNNAMED \
     -cp "$ALL_CLASSPATH" \
     rommanager.main.RomManager

echo ""
echo "RomManager closed."