# Первый этап: сборка приложения
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Копируем файлы Gradle из корня проекта
COPY ../gradlew ./gradlew
COPY ../gradle ./gradle
COPY ../settings.gradle.kts ./settings.gradle.kts
COPY ../build.gradle.kts ./build.gradle.kts
COPY ../gradle/libs.versions.toml ./gradle/libs.versions.toml

# Копируем исходный код сервиса
COPY . .

# Выполняем сборку приложения с помощью Gradle
RUN ./gradlew build --no-daemon

# Второй этап: минимальный образ для запуска
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем собранное приложение из первого этапа
COPY --from=build /app/order-service/build/libs/order-service-all.jar /app/app.jar


# Копируем файл application.yaml
COPY ./order-service/src/main/resources/application.yaml /app/application.yaml

# Указываем порт
EXPOSE 8081

# Команда запуска с указанием файла конфигурации
CMD ["java", "-jar", "/app/app.jar", "-config=/app/application.yaml"]

