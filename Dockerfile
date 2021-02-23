FROM openjdk:8-jdk as builder
LABEL maintainer="Hugo Josefson <hugo@josefson.org> (https://www.hugojosefson.com/)"

RUN mkdir /app
WORKDIR /app

## Cache mvn
COPY mvnw .
COPY .mvn ./.mvn
RUN ./mvnw -version

## Cache what downloaded deps we can
COPY pom.xml pom.xml
RUN ./mvnw dependency:go-offline --fail-never

## Include the rest
COPY . .

## Create production artifact
RUN ./mvnw install

#######################################################################################################################
FROM openjdk:8-jdk
LABEL maintainer="Hugo Josefson <hugo@josefson.org> (https://www.hugojosefson.com/)"

RUN mkdir /app
WORKDIR /app

COPY --from=builder /app/target/WorldRebuild.jar .

CMD bash -c "java -jar WorldRebuild.jar --version && echo ========== && java -jar WorldRebuild.jar"
