FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk21-temurin-jammy

RUN rm -rf /usr/local/tomcat/webapps/*

RUN sed -i 's/port="8005"/port="-1"/g' /usr/local/tomcat/conf/server.xml

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

RUN mkdir -p /tmp/uploads

EXPOSE 8080

# 6. Chạy Tomcat
CMD ["catalina.sh", "run"]