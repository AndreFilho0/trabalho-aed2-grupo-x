#!/bin/bash

set -e

echo "=========================================="
echo "SUBMETENDO JOB SPARK AO CLUSTER"
echo "=========================================="
echo ""

# Verificar se cluster está rodando
if ! docker-compose ps | grep -q "Up"; then
  echo "✗ Cluster não está rodando!"
  echo "Execute: ./scripts/run.sh"
  exit 1
fi

echo "Status dos containers:"
docker-compose ps

echo ""
echo "Iniciando aplicação..."
echo ""

# Trigger do driver
docker-compose exec -T app-driver /app/entrypoint.sh &

# Acompanhar logs
sleep 5
./scripts/logs.sh
