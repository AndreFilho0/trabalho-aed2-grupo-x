#!/bin/bash

echo "Reiniciando cluster..."

./scripts/stop.sh
sleep 3
./scripts/run.sh

echo "✓ Cluster reiniciado"
