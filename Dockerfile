
FROM openjdk:17-oracle

LABEL authors="sunday"

COPY target/bankapp-0.0.1-SNAPSHOT.jar.original bankapp-image.jar

EXPOSE 8085

EXPOSE 3306

ENTRYPOINT ["java", "-jar", "bankapp-image.jar"]