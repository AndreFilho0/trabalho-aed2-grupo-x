#!/bin/bash

set -e

echo "=========================================="
echo "Compilando Projeto Scala"
echo "=========================================="

docker-compose build --no-cache

echo ""
echo "✓ Build concluído com sucesso!"
