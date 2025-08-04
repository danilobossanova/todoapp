# Etapa 1: Build e Testes
FROM maven:3.9.6-eclipse-temurin-17 AS construtor
WORKDIR /app

# Copia apenas os arquivos de configuração primeiro para otimizar cache das dependências
COPY pom.xml ./
COPY src ./src

# Baixa dependências (layer cached separadamente)
RUN mvn dependency:go-offline -B

# Executa testes e gera o JAR (falha se testes falharem)
RUN mvn clean verify



# Etapa 2: Execução
FROM eclipse-temurin:17-jre-alpine AS executor

# Cria usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=construtor /app/target/todoapp-0.0.1-SNAPSHOT.jar app.jar

# Ajusta permissões
RUN chown -R appuser:appgroup /app

# Muda para usuário não-root
USER appuser

# Configura variáveis de ambiente para otimização da JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"

EXPOSE 8080

# Configura health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]