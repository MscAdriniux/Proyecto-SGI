# Etapa 1: Construcción (Compila tu proyecto en los servidores de Render)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Levanta el servidor con tu .jar)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/SistemaGestionIncidencias-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
