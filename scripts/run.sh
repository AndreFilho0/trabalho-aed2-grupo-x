#!/bin/bash

set -e

echo "=========================================="
echo "INICIANDO CLUSTER SPARK DISTRIBUÍDO"
echo "=========================================="
echo ""
echo "Componentes:"
echo "  ✓ 1 Spark Master"
echo "  ✓ 3 Spark Workers"
echo "  ✓ 1 Driver Application"
echo ""

docker-compose up -d

echo ""
echo "Aguardando inicialização dos containers..."
sleep 10

echo ""
echo "=========================================="
echo "✓ CLUSTER INICIADO COM SUCESSO"
echo "=========================================="
echo ""
echo "Portais de Monitoramento:"
echo "  - Spark Master UI:  http://localhost:8080"
echo "  - Spark Worker 1:   http://localhost:8081"
echo "  - Spark Worker 2:   http://localhost:8082"
echo "  - Spark Worker 3:   http://localhost:8083"
echo "  - Application UI:   http://localhost:4040 (durante execução)"
echo ""
echo "Para acompanhar o progresso:"
echo "  ./scripts/logs.sh"
echo ""
echo "Para parar o cluster:"
echo "  ./scripts/stop.sh"
echo ""
