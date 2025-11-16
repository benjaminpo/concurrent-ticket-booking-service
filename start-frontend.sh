#!/bin/bash

echo "=========================================="
echo "Starting Ticket Booking Service Frontend"
echo "=========================================="
echo ""

cd frontend

if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
    echo ""
fi

echo "Starting Angular development server..."
echo "Frontend will be available at: http://localhost:4200"
echo ""
echo "Make sure the backend is running on port 8080"
echo ""

npm start

