# Board Game PageRank Analysis com Spark GraphX

Análise distribuída de influência de jogos de tabuleiro usando PageRank em Apache Spark.

## Requisitos

- Docker >= 20.10
- Docker Compose >= 1.29
- 4GB RAM mínimo
- 2GB espaço em disco

##  Início Rápido

### 1. Clone e Configure


Perfeito! Aqui está a versão revisada do Markdown, sem emojis e sem a parte da cópia dos arquivos, já que os volumes estão compartilhados:

---

# Guia para Rodar PageRank Distribuído com Docker e Spark

Este guia descreve como rodar a análise de PageRank sobre o dataset de board games de forma distribuída usando Spark em containers Docker.

---

## 1. Pré-requisitos

* Docker e Docker Compose instalados.
* Boa conexão de internet (a primeira build vai baixar imagens e dependências).
* Sistema com pelo menos 4GB de RAM para suportar os containers Spark.

---

## 2. Rodando o cluster Spark

No diretório do projeto, execute:

```bash
docker-compose up --build -d
```

Observações:

* A primeira execução pode demorar, pois Docker irá baixar todas as imagens e construir os containers.
* Depois disso, a inicialização será mais rápida.

---

## 3. Acessando a interface do Spark

Abra o navegador em:

```
http://localhost:8080
```

* Aqui você verá o Spark Master e os workers.
* Espere até que todos os workers estejam ativos — pode demorar alguns instantes na primeira vez .

---

## 4. Submeter a análise PageRank

Entre no container do driver:

```bash
docker exec -it app-driver /bin/bash
```

Dentro do container, rode o comando para submeter o job Spark:

```bash
/opt/spark/bin/spark-submit \
  --master spark://spark-master:7077 \
  --deploy-mode cluster \
  --class com.boardgame.Main \
  /app/board-game-pagerank.jar
```

Observações:

* Ao submeter, você pode acompanhar o progresso na UI do Spark em `localhost:8080`.
* Cada worker poderá processar o grafo, dependendo de como o Spark distribuir as tarefas.

---

## 5. Resultados

* Os resultados serão salvos diretamente no volume compartilhado:

```
./data/output/pagerank-results.csv

```

---

## 6. Próximos passos / melhorias

* Explorar o REST API do Spark (`localhost:6066`) para submeter jobs de forma programática e facilitar testes e apresentações.
* Avaliar a possibilidade de dividir o grafo em subgrafos e processar em paralelo, depois combinar os resultados (map-reduce).
* Atualmente, todo o grafo é processado dentro de um único worker por vez, então a distribuição real ainda não ocorre.


