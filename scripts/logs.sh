#!/bin/bash

echo "=========================================="
echo "LOGS DO CLUSTER SPARK"
echo "=========================================="
echo ""
echo "Pressione Ctrl+C para sair"
echo ""

docker-compose logs -f --tail=50
