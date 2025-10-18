# Etapa de build
FROM maven:3.9.3-eclipse-temurin-19 AS builder

WORKDIR /app

# Copiar pom y código fuente
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn .mvn

# Build del jar (sin tests)
RUN ./mvnw clean package -DskipTests

# Etapa final
FROM eclipse-temurin:19-jdk

WORKDIR /app

# Copiar el jar generado en la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Puerto que Render asignará
ENV PORT=8080
EXPOSE $PORT

# Ejecutar el jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
