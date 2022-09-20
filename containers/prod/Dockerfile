FROM amazoncorretto:17-alpine3.15
WORKDIR "sitas-storagemanager-build"
COPY ./ ./
ARG DATABASE_ENDPOINT
ENV DATABASE_ENDPOINT=${DATABASE_ENDPOINT}
#ENTRYPOINT ["echo", "$DATABASE_ENDPOINT"]
ENTRYPOINT ["java", "-jar", "*.jar"]