FROM amazoncorretto:17-alpine3.15 AS prod
WORKDIR "/sitas-storagemanager-prod"

COPY ./ ./

RUN chmod +x entrypoint.sh

ENTRYPOINT ["sh", "entrypoint.sh"]