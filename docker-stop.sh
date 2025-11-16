#!/bin/bash

echo "=============================================="
echo "Stopping Ticket Booking Service Containers"
echo "=============================================="
echo ""

docker-compose down

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Containers stopped successfully!"
    echo ""
    echo "To remove images as well:"
    echo "  docker-compose down --rmi all"
    echo ""
    echo "To remove volumes as well:"
    echo "  docker-compose down -v"
else
    echo ""
    echo "❌ Failed to stop containers."
    exit 1
fi

