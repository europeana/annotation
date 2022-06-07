# Use same version as CloudFoundry deployment
FROM library/mongo:3.6.17

EXPOSE 27017

ENV MONGO_INITDB_ROOT_USERNAME admin
ENV MONGO_INITDB_ROOT_PASSWORD password

COPY init-mongo.sh /docker-entrypoint-initdb.d/init-mongo.sh