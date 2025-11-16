# Sistema de Recomendação de Jogos com Spark GraphX

Projeto da disciplina Algoritmos e Estruturas de Dados 2 (AED2) - Sistema de recomendação baseado em grafos usando Apache Spark GraphX.

## Pré-requisitos

- Docker instalado
- Docker Compose instalado
- 4GB de RAM disponível

## Como Rodar o Projeto

### 1. Clone o repositório
```bash
git clone <seu-repositorio>
cd trabalho-aed2-grafos
```

### 2. Construa e inicie o container
```bash
docker-compose up --build
```

### 3. Acesse o Jupyter Notebook

Abra seu navegador em: http://localhost:8888

### 4. Execute os notebooks na ordem

1. 01_teste_basico_spark.ipynb - Testa a instalação do Spark
2. 02_criando_grafo_basico.ipynb - Cria o grafo de jogos
3. 03_graphx_pagerank_communities.scala - Executa os algoritmos GraphX

## Estrutura do Projeto
```
trabalho-aed2-grafos/
├── Dockerfile                          # Configuração do ambiente
├── docker-compose.yml                  # Orquestração dos containers
├── README.md                           # Este arquivo
├── dados/
│   ├── boardgames_100.json            # Dataset original
│   ├── nodes.csv                      # Gerado: vértices do grafo
│   ├── edges.csv                      # Gerado: arestas do grafo
│   ├── results_pagerank.csv           # Gerado: resultados PageRank
│   └── results_communities.csv        # Gerado: comunidades
└── notebooks/
    ├── 01_teste_basico_spark.ipynb
    ├── 02_criando_grafo_basico.ipynb
    └── 03_graphx_pagerank_communities.scala
```

## Como Executar os Algoritmos GraphX (Scala)

### Opção 1: Via Jupyter (recomendado para apresentação)

Os notebooks Python preparam os dados e você pode visualizar os resultados.

### Opção 2: Via spark-shell (avançado)
```bash
# Entrar no container
docker exec -it spark-graphx-aed2 bash

# Executar o script Scala
spark-shell -i /app/notebooks/03_graphx_pagerank_communities.scala
```

## Algoritmos Implementados

### 1. PageRank (Popularidade Global)
- Identifica os jogos mais influentes no grafo
- Quanto mais recomendações um jogo recebe, maior seu rank

### 2. Label Propagation (Comunidades / Similaridade)
- Detecta grupos de jogos similares
- Agrupa jogos que são frequentemente recomendados juntos

### 3. Sistema de Recomendação
- Por Popularidade: recomenda jogos com alto PageRank
- Por Similaridade: recomenda jogos da mesma comunidade

## Dataset

Board Games Recommendation Dataset (Graph Drawing Contest 2023)
- 100 jogos de tabuleiro populares
- Relações de recomendação/similaridade
- Fonte: https://mozart.diei.unipg.it/gdcontest/2023/creative/

## Tecnologias Utilizadas

- Apache Spark 3.5.0
- GraphX (biblioteca de grafos do Spark)
- Python 3 (para notebooks)
- Scala (para GraphX)
- Docker (containerização)
- Jupyter Notebook (interface)

## Comandos Úteis
```bash
# Iniciar o projeto
docker-compose up

# Parar o projeto
docker-compose down

# Reconstruir após mudanças
docker-compose up --build

# Ver logs
docker-compose logs -f

# Entrar no container
docker exec -it spark-graphx-aed2 bash

# Acessar Spark UI
# http://localhost:4040 (após executar um job)
```

## Conceitos de AED2 Aplicados

- Representação de grafos (vértices e arestas)
- Algoritmos de centralidade (PageRank)
- Detecção de comunidades (Label Propagation)
- Grafos direcionados vs. não-direcionados
- Análise de redes complexas

## Troubleshooting

### Erro de memória
Aumente a memória no docker-compose.yml:
```yaml
environment:
  - SPARK_DRIVER_MEMORY=4g
```

### Porta já em uso
Mude a porta no docker-compose.yml:
```yaml
ports:
  - "8889:8888"  # Usa porta 8889 no host
```

### Container não inicia
```bash
docker-compose down -v
docker-compose up --build
```

## Licença

MIT License
```

## Estrutura Final de Diretórios
```
trabalho-aed2-grafos/
├── Dockerfile
├── docker-compose.yml
├── README.md
├── teste_rapido.sh
├── dados/
│   └── boardgames_100.json
└── notebooks/
    ├── 01_teste_basico_spark.ipynb
    ├── 02_criando_grafo_basico.ipynb
    └── 03_graphx_pagerank_communities.scala
