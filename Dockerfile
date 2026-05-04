FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем скомпилированные классы
COPY com/ ./com/

# Копируем библиотеки
COPY lib/ ./lib/

# Копируем SQL скрипт
COPY sql/ ./sql/

# Открываем порт
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-cp", ".:lib/*", "com.trainer.Launcher"]