#!/bin/bash

set -e

SPARK_HOME="/opt/spark"
SPARK_CONF_DIR="${SPARK_HOME}/conf"

# Criar arquivo de configuração
cat > "${SPARK_CONF_DIR}/spark-defaults.conf" << EOF
# Configurações de Spark
spark.driver.memory          1g
spark.executor.memory        1g
spark.executor.cores         2
spark.sql.shuffle.partitions 8
spark.graphx.numPartitions   8

# Logging
spark.eventLog.enabled       true
spark.eventLog.dir           /opt/spark/logs

# Network
spark.driver.blockManager.port    8888
spark.shuffle.service.port        8889
spark.master.rest.enabled         true
spark.master.rest.port            6066
EOF

if [[ "${SPARK_MASTER_REST_ENABLED}" =~ ^([Tt][Rr][Uu][Ee]|1)$ ]]; then
  REST_PORT=${SPARK_MASTER_REST_PORT:-6066}
  export SPARK_MASTER_OPTS="${SPARK_MASTER_OPTS} -Dspark.master.rest.enabled=true -Dspark.master.rest.port=${REST_PORT}"
fi

# Determinar o modo de execução
MODE=${SPARK_MODE:-"local"}

case $MODE in
  "master")
    echo "=========================================="
    echo "Iniciando SPARK MASTER"
    echo "=========================================="
    mkdir -p /opt/spark/logs
    cd $SPARK_HOME
    ./sbin/start-master.sh
    echo "✓ Spark Master iniciado em spark://$(hostname):7077"
    tail -f /opt/spark/logs/spark-*-org.apache.spark.deploy.master.Master-*.out
    ;;
  
  "worker")
    echo "=========================================="
    echo "Iniciando SPARK WORKER"
    echo "=========================================="
    
    # Aguardar Master ficar pronto
    MASTER_HOST=${SPARK_MASTER_URL#spark://}
    MASTER_HOST=${MASTER_HOST%:*}
    
    echo "Aguardando Master em ${MASTER_HOST}:7077..."
    for i in {1..30}; do
      if nc -z $MASTER_HOST 7077 2>/dev/null; then
        echo "✓ Master respondendo!"
        break
      fi
      echo "  Tentativa $i/30..."
      sleep 2
    done
    
    mkdir -p /opt/spark/work /opt/spark/logs
    cd $SPARK_HOME
    ./sbin/start-worker.sh $SPARK_MASTER_URL
    echo "✓ Spark Worker iniciado conectado a $SPARK_MASTER_URL"
    tail -f /opt/spark/logs/spark-*-org.apache.spark.deploy.worker.Worker-*.out
    ;;
  
  "driver")
    echo "=========================================="
    echo "DRIVER APPLICATION - Aguardando Cluster"
    echo "=========================================="
    
    # Aguardar cluster ficar pronto
    MASTER_HOST=${SPARK_MASTER_URL#spark://}
    MASTER_HOST=${MASTER_HOST%:*}
    
    echo "Aguardando Spark Master em ${MASTER_HOST}:7077..."
    for i in {1..60}; do
      if nc -z $MASTER_HOST 7077 2>/dev/null; then
        echo "✓ Cluster pronto!"
        break
      fi
      echo "  Tentativa $i/60..."
      sleep 1
    done
    
    sleep 3
    
    echo ""
    echo "=========================================="
    echo "Executando Job Spark"
    echo "=========================================="
    echo "Master: $SPARK_MASTER_URL"
    echo "Input: $INPUT_PATH"
    echo "Output CSV: $OUTPUT_CSV"
    echo "Output JSON: $OUTPUT_JSON"
    echo "=========================================="
    echo ""
    
    $SPARK_HOME/bin/spark-submit \
      --class com.boardgame.Main \
      --master $SPARK_MASTER_URL \
      --driver-memory 1g \
      --executor-memory 1g \
      --executor-cores 2 \
      --num-executors 3 \
      --conf "spark.sql.shuffle.partitions=8" \
      --conf "spark.graphx.numPartitions=8" \
      /app/board-game-pagerank.jar
    
    echo ""
    echo "=========================================="
    echo "JOB CONCLUÍDO"
    echo "=========================================="
    
    # Manter container ativo
    tail -f /dev/null
    ;;
  
  *)
    echo "Modo desconhecido: $MODE"
    echo "Modos válidos: master, worker, driver"
    /bin/bash
    ;;
esac
