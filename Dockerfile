# Stage 1: construir el jar
FROM openjdk:19-jdk-slim AS builder

WORKDIR /app

# Copiar Maven wrapper y pom.xml primero
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

# Copiar código fuente
COPY src src

# Dar permisos a mvnw y compilar jar
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: imagen final
FROM eclipse-temurin:19-jdk

WORKDIR /app

# Copiar el jar compilado del stage builder y renombrarlo a app.jar
COPY --from=builder /app/target/dongfang-0.0.1-SNAPSHOT.jar app.jar

# Puerto que Render asigna
ENV PORT=8080
EXPOSE $PORT

# Variable de entorno con credenciales de Google Sheets
# REEMPLAZÁ el valor por tu JSON completo de credenciales
# Render permite definirla en Environment → Add Environment Variable
ENV GOOGLE_SHEETS_CREDENTIALS="{}"

ENTRYPOINT ["java","-jar","/app/app.jar"]
