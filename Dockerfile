FROM eclipse-temurin:11-jdk-jammy as builder

WORKDIR /build

# Instalar SBT
RUN apt-get update && apt-get install -y \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

RUN curl -L https://github.com/sbt/sbt/releases/download/v1.9.6/sbt-1.9.6.tgz \
    -o /tmp/sbt.tgz && \
    tar -xzf /tmp/sbt.tgz -C /opt/ && \
    mv /opt/sbt /opt/sbt-1.9.6 && \
    ln -s /opt/sbt-1.9.6 /opt/sbt && \
    chmod +x /opt/sbt/bin/sbt && \
    rm /tmp/sbt.tgz

ENV SBT_HOME=/opt/sbt
ENV PATH=$PATH:$SBT_HOME/bin

COPY build.sbt .
COPY project ./project
COPY src ./src

RUN sbt clean assembly

FROM spark:3.5.7-scala2.12-java11-ubuntu

WORKDIR /app

# Copiar JAR
COPY --from=builder /build/target/board-game-pagerank.jar /app/

# Criar diretórios usados no entrypoint
RUN mkdir -p /app/data/raw /app/data/processed /app/data/output /app/logs

# Criar diretório e arquivo de configuração do Spark usados no entrypoint.sh
RUN mkdir -p /opt/spark/conf && \
    touch /opt/spark/conf/spark-defaults.conf

# Corrigir permissões do diretório de logs do Spark
RUN mkdir -p /opt/spark/logs && \
    chmod -R 777 /opt/spark/logs

# Copiar entrypoint
COPY --chmod=755 entrypoint.sh /app/

# Expor portas
EXPOSE 8080 8081 8082 8083 7077 7001 7002 7003 4040 6066

ENTRYPOINT ["/app/entrypoint.sh"]


