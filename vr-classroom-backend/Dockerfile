# 使用 Eclipse Temurin 替代弃用的 openjdk
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的jar文件
COPY target/vr-classroom-backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]
