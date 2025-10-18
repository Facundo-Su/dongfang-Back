# Usamos JDK 19
FROM openjdk:19-jdk-slim

# Directorio de la app dentro del contenedor
WORKDIR /app

# Copiar el jar compilado al contenedor
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Exponer el puerto que Render le asigna
ENV PORT=8080
EXPOSE $PORT

# Comando para ejecutar el jar
ENTRYPOINT ["java","-jar","/app.jar"]
