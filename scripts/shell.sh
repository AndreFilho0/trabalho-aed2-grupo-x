#!/bin/bash

CONTAINER=${1:-"app-driver"}

echo "Acessando shell do container: $CONTAINER"
echo ""

docker-compose exec $CONTAINER /bin/bash
