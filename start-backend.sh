#!/bin/bash

echo "========================================="
echo "Starting Ticket Booking Service Backend"
echo "========================================="
echo ""

# Set Java 21 as JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
echo "Using Java version: $(java -version 2>&1 | head -n 1)"
echo ""

cd backend

echo "Building the project..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "Starting Spring Boot application..."
    echo "Backend will be available at: http://localhost:8080"
    echo "H2 Console: http://localhost:8080/h2-console"
    echo ""
    mvn spring-boot:run
else
    echo ""
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

