#!/bin/bash

echo "=========================================="
echo "STATUS DO CLUSTER SPARK"
echo "=========================================="
echo ""

echo "Containers:"
docker-compose ps

echo ""
echo "Portas em uso:"
echo "  Master:    $(nc -zv localhost 7077 2>&1 | grep -o 'succeeded\|refused')"
echo "  Worker 1:  $(nc -zv localhost 7001 2>&1 | grep -o 'succeeded\|refused')"
echo "  Worker 2:  $(nc -zv localhost 7002 2>&1 | grep -o 'succeeded\|refused')"
echo "  Worker 3:  $(nc -zv localhost 7003 2>&1 | grep -o 'succeeded\|refused')"

echo ""
echo "URLs de acesso:"
echo "  Master UI:    http://localhost:8080"
echo "  Worker 1 UI:  http://localhost:8081"
echo "  Worker 2 UI:  http://localhost:8082"
echo "  Worker 3 UI:  http://localhost:8083"

echo ""
echo "Para investigar um container:"
echo "  docker-compose logs spark-master"
echo "  docker-compose logs spark-worker-1"
echo "  docker-compose logs app-driver"
