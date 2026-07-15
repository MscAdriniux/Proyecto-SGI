# Etapa 1: Construcción (Compila tu proyecto en los servidores de Render)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Levanta el servidor con tu .jar)
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Proyecto-SGI-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
