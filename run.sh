#!/bin/bash

# Compile and run Library Management System

echo ""
echo "===== Library Management System ====="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven not found!"
    echo "Please install Maven first."
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found!"
    echo "Please install Java 11 or higher."
    exit 1
fi

# Show Java version
echo "Java version:"
java -version
echo ""

# Clean and build
echo "Building project..."
mvn clean compile package

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi

echo ""
echo "===== Build successful! ====="
echo ""
echo "Starting application..."
echo ""

# Run the application
java -cp target/library-management-1.0-jar-with-dependencies.jar com.library.gui.MainFrame
