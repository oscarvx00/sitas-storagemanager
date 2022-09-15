FROM amazoncorretto:17-alpine3.15
WORKDIR "sitas-storagemanager-build"
COPY ./ ./
ENTRYPOINT ["echo", "$DATABASE_ENDPOINT"]
#ENTRYPOINT ["java", "-jar", "*.jar"]