#!/bin/bash

echo "============================================"
echo "Running Ticket Booking Service Tests"
echo "============================================"
echo ""

# Set Java 21 as JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
echo "Using Java version: $(java -version 2>&1 | head -n 1)"
echo ""

# Check if running in Docker or locally
if [ -f "docker-compose.yml" ] && docker-compose ps | grep -q "backend"; then
    echo "⚠️  Note: Tests must be run locally (Maven not available in Docker runtime container)"
    echo ""
    echo "To run tests:"
    echo "  1. Stop Docker: docker-compose down"
    echo "  2. Run locally: cd backend && mvn test"
    echo ""
    echo "Proceeding with local test execution..."
    echo ""
fi

cd backend

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    echo ""
    echo "Please install Maven 3.6+ to run tests locally"
    echo "Or run tests during Docker build: docker-compose build backend"
    exit 1
fi

echo "Running JUnit tests..."
echo "This includes concurrent booking scenario tests"
echo ""

mvn test

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ All tests passed!"
else
    echo ""
    echo "❌ Some tests failed. Please check the output above."
    exit 1
fi

