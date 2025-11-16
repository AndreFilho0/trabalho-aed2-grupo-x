

#   — Sistema de Recomendação de Jogos com Spark GraphX

# **Sistema de Recomendação de Jogos de Tabuleiro usando Grafos (Spark GraphX)**

Este projeto implementa um **sistema de recomendação baseado em grafos** utilizando o **Apache Spark GraphX** e o **dataset "Board Games Recommendation: Graph Drawing Contest 2023"**.

O objetivo é demonstrar a aplicação prática de conceitos estudados na disciplina de **Algoritmos e Estruturas de Dados 2 (AED2)**, especialmente:

* Estruturas de grafos
* Algoritmos de centralidade
* Algoritmos de detecção de comunidades
* Raciocínio baseado em redes

---

#  **Objetivo do Projeto**

Criar um sistema que recomende jogos de tabuleiro com base em dois critérios principais:

---

## **1. Recomendações por Popularidade (PageRank)**

Usamos o algoritmo **PageRank** do GraphX para determinar quais jogos são mais influentes/populares no grafo.

A ideia:

> Quanto mais um jogo é recomendado por outros jogos (especialmente jogos importantes), maior é sua pontuação.

O sistema permite:

* Listar os jogos mais influentes globalmente
* Recomendar jogos populares no contexto das preferências do usuário

---

## **2. Recomendações por Similaridade (Comunidades no Grafo)**

Usando algoritmos de detecção de comunidades, conseguimos agrupar jogos em grupos de similaridade.

Algoritmos considerados:

* **Connected Components**
* **Label Propagation (LPA)**
* **Strongly Connected Components (SCC)**
* **Triangle Count** (para medir densidade local)

Selecionamos para o sistema final:

### **✔ Label Propagation (LPA)**

Porque:

* Identifica comunidades de forma rápida e eficiente
* Funciona bem em grafos grandes
* Gera grupos coesos de jogos similares

Com isso, dado um jogo X, o sistema:

1. Identifica a comunidade à qual X pertence
2. Lista outros jogos dessa comunidade
3. Ordena-os pela influência (PageRank) para melhorar a recomendação

---

#  **Dataset Usado**

**Board Games Recommendation Dataset (Graph Drawing Contest 2023)**
- LINK : https://mozart.diei.unipg.it/gdcontest/2023/creative/

O dataset contém:

* **Vértices:** jogos de tabuleiro
* **Arestas:** relações de recomendação/similaridade entre jogos
* **Possíveis atributos:** id, nome, nota, categoria etc.

| Critério                                      | Dataset atende? | Justificativa                              |
| --------------------------------------------- | --------------- | ------------------------------------------ |
| Grafo claro                                   | ✔               | Nós = jogos, arestas = recomendações       |
| Funciona com PageRank                         | ✔               | Grafo dirigido, perfeito para centralidade |
| Funciona com Comunidades                      | ✔               | LPA gera clusters naturais                 |
| Pequeno e eficiente                           | ✔               | Top-40 ou Top-100 → leve para GraphX       |
| Visualmente compreensível                     | ✔               | Dá para fazer gráficos bonitos             |
| Relaciona-se diretamente ao tema do professor | ✔               | É um problema real de recomendação         |
| Fácil de explicar                             | ✔               | Jogo A → Jogo B básico                     |

Nós tratamos o dataset em dois arquivos:

```
dados/
 ├── nodes.csv   (lista de jogos)
 └── edges.csv   (recomendações entre jogos)
```

Cada jogo é representado como um vértice, e cada relação de recomendação é uma aresta no grafo.

---

#  **Modelagem em Grafos**

### **Vértices (nodes.csv):**

* id
* nome do jogo
* (opcional: nota, complexidade etc.)

### **Arestas (edges.csv):**

* id_jogo_origem
* id_jogo_destino
* peso (opcional → 1.0 por padrão)

A construção do grafo usa:

```scala
val graph = Graph(verticesRDD, edgesRDD)
```

---

#  **Arquitetura do Sistema**

```
Dataset CSV
   ↓
Spark DataFrames
   ↓
GraphX Graph
   ↓
PageRank + Label Propagation
   ↓
Sistema de Recomendação
```

---

#  **Algoritmos Utilizados**

### **1. PageRank (popularidade/global influence)**

* Mede a importância de cada jogo no grafo
* Retorna uma pontuação (rank) para cada jogo

### **2. Label Propagation – LPA (detecção de comunidades / similaridade)**

* Descobre grupos de jogos similares
* Baseado em propagação de rótulos

### **3. Coletar vizinhos (expansão local do grafo)**

Usado para recomendações contextuais.

---

#  **Funcionamento do Sistema de Recomendação**

Dado um jogo **X**, fazemos:

---

### **A) Recomendação baseada em popularidade**

1. Pegamos a vizinhança de X (jogos conectados)
2. Recuperamos o PageRank de cada um
3. Ordenamos do mais influente ao menos influente
4. Retornamos **os jogos mais populares próximos de X**

---

### **B) Recomendação baseada em similaridade**

1. Identificamos a comunidade do jogo X via LPA
2. Pegamos todos os jogos desta comunidade
3. Ordenamos pelo PageRank
4. Retornamos **jogos similares com maior influência**

---

#  **Exemplo Simplificado de Saída**

```
Recomendações para: "Catan"

→ Baseado em Popularidade (PageRank):
1) Pandemic
2) Ticket to Ride
3) Carcassonne

→ Baseado em Similaridade (Comunidade):
1) Stone Age
2) Lords of Waterdeep
3) Isle of Skye
```

---

#  **Tecnologias Usadas**

* **Apache Spark 3.x**
* **GraphX**
* **Scala**
* **Dataset em CSV**

---

#  **Estrutura do Projeto**

```
trabalho-aed2-grupo-x/
 ├── apresentacao-grupo-X.pdf
 ├── README.md
 ├── dados/
 │   ├── nodes.csv
 │   └── edges.csv
 └── src/
     ├── Main.scala
     ├── GraphBuilder.scala
     ├── PageRankModule.scala
     ├── CommunityModule.scala
     ├── Recommender.scala
     └── Utils.scala
```

---

#  **Conclusão**

Este projeto mostra como algoritmos clássicos de grafos podem ser aplicados para criar um sistema eficiente de recomendação de jogos de tabuleiro.
Utilizando PageRank e Label Propagation em conjunto, conseguimos:

* identificar jogos influentes/populares
* agrupar jogos similares
* gerar recomendações personalizadas baseadas na estrutura do grafo

O uso de Spark GraphX torna o sistema escalável e capaz de lidar com datasets grandes, destacando sua aplicação no mundo real.

---
