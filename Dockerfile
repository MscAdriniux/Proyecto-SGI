# Etapa 1: Construcción (Compila tu proyecto en los servidores de Render)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Levanta el servidor con tu .jar)
FROM openjdk:17.0.1-jdk-slim
# AQUÍ ESTÁ EL CAMBIO MAGICO: Usamos el nombre real del archivo
COPY --from=build /target/SistemaGestionIncidencias-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
