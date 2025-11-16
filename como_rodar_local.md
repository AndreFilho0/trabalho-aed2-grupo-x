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

1. 01.ipynb - gera os dados prontos para análise (pré-processamento e criação do grafo).
2. 02.ipynb -confirma que esses dados foram gerados corretamente.
3. 03_graphx_pagerank_communities.scala - Executa os algoritmos GraphX
4. 04.ipynb - faz a criação das tabelas em csv e agrupa os dados de LPA 

## Estrutura do Projeto
```
Dockerfile
├── LICENSE
├── README.md
├── como_rodar_local.md
├── dados
│   ├── boardgames_100.json
│   ├── edges.csv
│   │   ├── _SUCCESS
│   │   └── part-00000-04ed26ea-f3cb-424f-9ce6-9f2a6cbeff15-c000.csv
│   ├── lpa_grouped_all.csv
│   ├── nodes.csv
│   │   ├── _SUCCESS
│   │   └── part-00000-92ddd861-7457-4034-88ef-749d72549d18-c000.csv
│   ├── output_lpa
│   │   ├── _SUCCESS
│   │   └── part-00000-f404ab64-b159-43e9-a06e-484d637f83f3-c000.csv
│   ├── output_pagerank
│   │   ├── _SUCCESS
│   │   └── part-00000-43872037-9d2f-4470-be19-d7dc07e3a574-c000.csv
│   └── pagerank_all.csv
├── docker-compose.yml
├── notebooks
│   ├── 01.ipynb
│   ├── 02.ipynb
│   ├── 03_graphx_pagerank_communities.scala
│   ├── 04.ipynb
│   ├── lpa_graph.png
│   └── pagerank_graph.png
├── src
└── teste_rapido.sh
```

## Como Executar os Algoritmos GraphX (Scala)

### Opção 1: Via Jupyter :

Os notebooks Python preparam os dados e você pode visualizar os resultados , rode os scripts 01 e 02 para preparar os dados  , o 4 é para montar a tabela 


### Opção 2: Via spark-shell rodar o codigo em scala para pode rodar o pagaRank e o LPA
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
Dockerfile
├── LICENSE
├── README.md
├── como_rodar_local.md
├── dados
│   ├── boardgames_100.json
│   ├── edges.csv
│   │   ├── _SUCCESS
│   │   └── part-00000-04ed26ea-f3cb-424f-9ce6-9f2a6cbeff15-c000.csv
│   ├── lpa_grouped_all.csv
│   ├── nodes.csv
│   │   ├── _SUCCESS
│   │   └── part-00000-92ddd861-7457-4034-88ef-749d72549d18-c000.csv
│   ├── output_lpa
│   │   ├── _SUCCESS
│   │   └── part-00000-f404ab64-b159-43e9-a06e-484d637f83f3-c000.csv
│   ├── output_pagerank
│   │   ├── _SUCCESS
│   │   └── part-00000-43872037-9d2f-4470-be19-d7dc07e3a574-c000.csv
│   └── pagerank_all.csv
├── docker-compose.yml
├── notebooks
│   ├── 01.ipynb
│   ├── 02.ipynb
│   ├── 03_graphx_pagerank_communities.scala
│   ├── 04.ipynb
│   ├── lpa_graph.png
│   └── pagerank_graph.png
├── src
└── teste_rapido.sh
    
