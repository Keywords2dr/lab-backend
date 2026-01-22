# --- Giai đoạn 1: Build file .jar bằng Maven ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Lệnh build, bỏ qua test
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy file .jar từ giai đoạn 1 sang
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục upload tạm
RUN mkdir -p /tmp/uploads

# Mở cổng 8080
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]