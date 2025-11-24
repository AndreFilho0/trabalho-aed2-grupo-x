#!/bin/bash

set -e

echo "=========================================="
echo "BUILDANDO CONTEINER"
echo "=========================================="

docker compose build --no-cache


echo "=========================================="
echo "INICIANDO CONTEINERES"
echo "=========================================="
docker compose up -d

echo "=========================================="
echo "EXECUTANDO DRIVER NO CONTEINER"
echo "=========================================="

docker exec -it app-driver /bin/bash -c "\
/opt/spark/bin/spark-submit \
  --master spark://spark-master:7077 \
  --deploy-mode cluster \
  --class com.boardgame.Main \
  /app/board-game-pagerank.jar"

echo "=========================================="
echo "DRIVER EXECUTADO COM SUCESSO"
echo "=========================================="
