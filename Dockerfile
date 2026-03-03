FROM bellsoft/liberica-openjdk-alpine:17
# ---------- Build Stage ----------


WORKDIR /app

COPY . .
RUN ./mvnw clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Crear usuario seguro
RUN useradd -m springuser

# Copiar jar
COPY --from=builder /app/target/*.jar app.jar

# Cambiar propietario
RUN chown -R springuser:springuser /app

USER springuser

# 🔥 Cambiamos el puerto interno
ENV SERVER_PORT=9090

EXPOSE 9090

ENTRYPOINT ["java","-jar","app.jar"]