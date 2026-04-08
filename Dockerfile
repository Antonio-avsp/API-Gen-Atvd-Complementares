# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro para cachear as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Variáveis de ambiente com valores padrão (Railway injeta as reais)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]