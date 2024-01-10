# Builds a docker image from a locally built Maven war. Requires 'mvn package' to have been run beforehand
# when needed the image can be switched from jre to jdk, but the jdk has about 200MB
#FROM eclipse-temurin:11-jdk-alpine
FROM eclipse-temurin:17-jre-alpine
LABEL Author="Europeana Foundation <development@europeana.eu>"

# Configure APM and add APM agent
ENV ELASTIC_APM_VERSION 1.34.1
#disabled by default, to be enabled by kustomize/build params for specific servers only
ENV ELASTIC_APM_ENABLED false
ADD https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/$ELASTIC_APM_VERSION/elastic-apm-agent-$ELASTIC_APM_VERSION.jar  /opt/app/elastic-apm-agent.jar

COPY ./annotation-web/target/annotation-web-executable.jar /opt/app/annotation-web-executable.jar
EXPOSE 8080
ENTRYPOINT ["java", "--add-opens java.base/java.util=ALL-UNNAMED" , "-jar", "/opt/app/annotation-web-executable.jar"]