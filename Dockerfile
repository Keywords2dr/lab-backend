# --- Giai đoạn 1: Build file .war bằng Maven ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Build ra file .war
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng bằng Tomcat ---
FROM tomcat:10.1-jdk21-temurin-jammy

# Xóa các ứng dụng mặc định của Tomcat cho nhẹ
RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Tạo thư mục upload tạm
RUN mkdir -p /tmp/uploads

# Tomcat mặc định chạy cổng 8080
EXPOSE 8080

# Lệnh chạy Tomcat
CMD ["catalina.sh", "run"]