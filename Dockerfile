FROM eclipse-temurin:11-jdk-jammy

# Instalar dependências básicas
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    python3 \
    python3-pip \
    procps \
    && rm -rf /var/lib/apt/lists/*

# Variáveis de ambiente
ENV SPARK_VERSION=3.5.0
ENV HADOOP_VERSION=3
ENV SPARK_HOME=/opt/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
ENV PYSPARK_PYTHON=python3

# Baixar e instalar Spark
RUN wget -q https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz \
    && tar -xzf spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz \
    && mv spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} ${SPARK_HOME} \
    && rm spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

# Instalar bibliotecas Python
RUN pip3 install --no-cache-dir \
    pyspark==${SPARK_VERSION} \
    jupyter \
    notebook \
    pandas \
    matplotlib \
    seaborn \
    networkx

# Criar diretório de trabalho
WORKDIR /app

# Copiar dados e notebooks
COPY dados /app/dados
COPY notebooks /app/notebooks

# Expor portas
# 8888 - Jupyter Notebook
# 4040 - Spark UI
EXPOSE 8888 4040

# Script de inicialização
CMD ["jupyter", "notebook", "--ip=0.0.0.0", "--port=8888", "--no-browser", "--allow-root", "--NotebookApp.token=''", "--NotebookApp.password=''"]


