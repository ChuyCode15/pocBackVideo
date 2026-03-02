FROM bellsoft/liberica-openjdk-alpine:17
RUN apk add --no-cache curl
WORKDIR /app


RUN mkdir -p /app/data /app/storage

COPY target/*.jar app.jar


ENV DB_STORAGE_VIDEO=/app/storage
ENV STORAGE_NGIX=http://20.228.66.108:7400/storage

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
