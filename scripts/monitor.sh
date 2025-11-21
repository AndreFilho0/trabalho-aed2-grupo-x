#!/bin/bash

echo "=========================================="
echo "MONITORAMENTO EM TEMPO REAL"
echo "=========================================="
echo ""

watch -n 2 'echo "=== CONTAINERS ===" && \
docker-compose ps && \
echo "" && \
echo "=== RECURSOS ===" && \
docker stats --no-stream spark-master spark-worker-1 spark-worker-2 spark-worker-3 app-driver 2>/dev/null || echo "Containers não encontrados"'
