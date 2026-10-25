#!/bin/bash
set -e

echo "=============================================================================="
echo "Oxford International School CSMS - Launching Containerized App"
echo "Database Topology: Local MySQL on host (3306), App inside Docker (8080)"
echo "=============================================================================="

if ! docker info >/dev/null 2>&1; then
    echo "[ERROR] Docker daemon is not running. Please start Docker first."
    exit 1
fi

echo "Building and starting Oxford CSMS container..."
docker compose up -d --build

echo ""
echo "Checking container status..."
docker ps --filter "name=oxford-csms-app"

echo ""
echo "=============================================================================="
echo "Oxford CSMS is launching at: http://localhost:8080"
echo "Connected to local MySQL at: host.docker.internal:3306/school"
echo "=============================================================================="
echo "To view live logs, run: docker logs -f oxford-csms-app"
echo "To stop container, run:  docker compose down"
echo "=============================================================================="
