#!/bin/bash

echo "Script de Teste Rápido - Spark GraphX"
echo "=========================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função de verificação
check_status() {
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK: $1${NC}"
  else
    echo -e "${RED}ERRO: $1 FALHOU${NC}"
    exit 1
  fi
}

# 1. Verificar se Docker está rodando
echo -e "${YELLOW}1. Verificando Docker...${NC}"
docker --version >/dev/null 2>&1
check_status "Docker instalado"

# 2. Verificar Docker Compose
echo -e "${YELLOW}2. Verificando Docker Compose...${NC}"
docker-compose --version >/dev/null 2>&1
check_status "Docker Compose instalado"

# 3. Verificar arquivo de dados
echo -e "${YELLOW}3. Verificando dataset...${NC}"
if [ -f "dados/boardgames_100.json" ]; then
  echo -e "${GREEN}OK: Dataset encontrado${NC}"
else
  echo -e "${RED}ERRO: Dataset não encontrado em dados/boardgames_100.json${NC}"
  exit 1
fi

# 4. Construir imagem Docker
echo -e "${YELLOW}4. Construindo imagem Docker...${NC}"
docker-compose build
check_status "Build da imagem Docker"

# 5. Iniciar container
echo -e "${YELLOW}5. Iniciando container...${NC}"
docker-compose up -d
check_status "Container iniciado"

# 6. Aguardar Jupyter inicializar
echo -e "${YELLOW}6. Aguardando Jupyter inicializar...${NC}"
sleep 10

# 7. Testar acesso ao Jupyter
echo -e "${YELLOW}7. Testando acesso ao Jupyter...${NC}"
curl -s http://localhost:8888 >/dev/null
check_status "Jupyter acessível"

# 8. Verificar logs
echo -e "${YELLOW}8. Últimos logs do container:${NC}"
docker-compose logs --tail=20

echo ""
echo -e "${GREEN}=========================================="
echo "TODOS OS TESTES PASSARAM!"
echo "==========================================${NC}"
echo ""
echo "Acesse: http://localhost:8888"
echo ""
echo "Para parar: docker-compose down"
echo ""
