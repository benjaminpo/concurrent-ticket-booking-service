#!/bin/bash

echo "=============================================="
echo "Starting Ticket Booking Service with Docker"
echo "=============================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install it and try again."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Check architecture
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    echo "🍎 Detected Apple Silicon (ARM64)"
    echo "   Using native ARM64 images for optimal performance"
    echo ""
fi

# Build and start containers
echo "Building and starting containers..."
echo "This may take a few minutes on the first run..."
echo ""
echo "Note: First build downloads ~2GB of images"
echo ""

docker-compose up --build -d

if [ $? -eq 0 ]; then
    echo ""
    echo "=============================================="
    echo "✅ Containers started successfully!"
    echo "=============================================="
    echo ""
    echo "📦 Services:"
    echo "  - Frontend: http://localhost:4200"
    echo "  - Backend:  http://localhost:8080"
    echo "  - API:      http://localhost:8080/api/events"
    echo ""
    echo "📊 View logs:"
    echo "  docker-compose logs -f"
    echo ""
    echo "🛑 Stop services:"
    echo "  docker-compose down"
    echo ""
    echo "Waiting for services to be healthy..."
    echo ""
    
    # Wait for services to be healthy
    sleep 10
    
    # Check backend health
    echo "Checking backend health..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/events > /dev/null 2>&1; then
            echo "✅ Backend is healthy!"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "⚠️  Backend may still be starting up. Check logs with: docker-compose logs backend"
        fi
        sleep 2
    done
    
    echo ""
    echo "🌐 Open your browser to http://localhost:4200"
    echo ""
else
    echo ""
    echo "❌ Failed to start containers. Check the error messages above."
    exit 1
fi

