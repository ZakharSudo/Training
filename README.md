# 🎯 Тренажёр аналитика | Backend

Бэкенд-сервис для образовательной платформы, где аналитики отрабатывают практические навыки в безопасной и контролируемой среде.
---

## 📋 О проекте

Проект представляет собой REST API сервис для тренажёра аналитиков, который позволяет:
- Проходить задания трёх типов (тесты, поиск ошибок, открытые задания)
- Получать автоматическую оценку результатов
- Отслеживать прогресс обучения в личном кабинете
- Сохранять все результаты в базе данных

### Целевая аудитория
- Junior и Middle аналитики, готовящиеся к собеседованиям
- Студенты профильных специальностей (аналитика, бизнес-информатика, ИТ)
- Специалисты смежных областей, переходящие в аналитику
- Корпоративные команды для внутреннего обучения

### Основные задачи проекта
- ✅ Регистрация и аутентификация пользователей
- ✅ Просмотр списка доступных тренажёров
- ✅ Прохождение заданий трёх типов
- ✅ Фиксация результатов выполнения
- ✅ Начисление баллов по единым правилам
- ✅ Хранение и отображение прогресса пользователя

---

## 🚀 Технологии

| Технология | Версия | Назначение |
|------------|--------|------------|
| Java | 17 | Основной язык разработки |
| PostgreSQL | 15 | Реляционная база данных |
| Gson | 2.10.1 | Сериализация/десериализация JSON |
| Docker | Latest | Контейнеризация приложения |
| JDBC | - | Подключение к базе данных |

---

## 📦 Установка и запуск

### Требования
- Java 17 или выше
- PostgreSQL 15 или выше
- Docker (опционально)
- Git

### Локальный запуск

```bash
# 1. Клонировать репозиторий
git clone https://github.com/ZakharSudo/Training.git
cd trainer-backend

# 2. Создать базу данных PostgreSQL
psql -U postgres -c "CREATE DATABASE trainer_db;"
psql -U postgres -c "CREATE USER trainer WITH PASSWORD 'trainer123';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE trainer_db TO trainer;"

# 3. Выполнить init.sql для создания таблиц и заполнения тестовыми данными
psql -U postgres -d trainer_db -f init.sql

# 4. Создать папку для зависимостей
mkdir lib

# 5. Скачать необходимые JAR файлы в папку lib
# PostgreSQL JDBC Driver: https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
# Gson: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# 6. Скомпилировать проект
javac -d out -cp "lib/*" src/main/java/com/trainer/*.java src/main/java/com/trainer/**/*.java

# 7. Запустить сервер
java -cp "out;lib/*" com.trainer.Launcher

---

Запуск через Docker (рекомендуется)
bash
# 1. Собрать и запустить контейнеры
docker-compose up -d

# 2. Проверить работу сервера
curl http://localhost:8888/ping

# 3. Просмотреть логи
docker-compose logs -f

# 4. Остановить контейнеры
docker-compose down

# 5. Остановить с удалением томов (очистка БД)
docker-compose down -v

-----

После запуска сервера вы увидите:

text
==========================================
🚀 Сервер тренажёра аналитика запущен!
==========================================
📍 Адрес: http://localhost:8888

📋 Доступные эндпоинты:
  GET    /ping                      - тест
  POST   /api/register              - регистрация
  POST   /api/login                 - вход
  GET    /api/tasks                 - список заданий
  POST   /api/tasks/{id}/submit     - отправить ответ
  GET    /api/profile               - мой прогресс
==========================================

-----

1. Проверка работоспособности
bash
curl http://localhost:8888/ping
Ответ:

json
{"pong":true}
2. Регистрация пользователя
bash
curl -X POST http://localhost:8888/api/register \
  -H "Content-Type: application/json" \
  -d '{"email":"student@test.com","password":"123456","fullName":"Иван Иванов"}'
Ответ:

json
{
  "success": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "student@test.com",
  "role": "STUDENT"
}

3. Вход в систему
bash
curl -X POST http://localhost:8888/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@test.com","password":"123456"}'
Ответ:

json
{
  "success": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "student@test.com",
  "role": "STUDENT",
  "token": "04c00772-a332-4f88-9030-1bd3ed6e120d"
}
4. Получение списка заданий
bash
curl http://localhost:8888/api/tasks
Ответ:

json
{
  "success": true,
  "tasks": [
    {
      "id": "11111111-1111-1111-1111-111111111111",
      "title": "Нефункциональные требования",
      "description": "Какие из перечисленных требований относятся к НЕФУНКЦИОНАЛЬНЫМ?",
      "type": "TEST",
      "maxPoints": 10
    },
    {
      "id": "22222222-2222-2222-2222-222222222222",
      "title": "UML диаграммы бизнес-процессов",
      "description": "Какие диаграммы UML используются для описания бизнес-процессов?",
      "type": "TEST",
      "maxPoints": 10
    }
  ]
}
5. Прохождение тестового задания
bash
curl -X POST http://localhost:8888/api/tasks/11111111-1111-1111-1111-111111111111/submit \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{"answer":"a1111111-1111-1111-1111-111111111111,a3111111-1111-1111-1111-111111111111"}'
Ответ:

json
{
  "success": true,
  "pointsAwarded": 10,
  "feedback": "Вы получили 10 из 10 баллов"
}
6. Прохождение задания на поиск ошибок
bash
curl -X POST http://localhost:8888/api/tasks/44444444-4444-4444-4444-444444444444/submit \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{"answer":"нет критериев измеримости, не указана роль для оформления"}'
Ответ:
json
{
  "success": true,
  "pointsAwarded": 15,
  "feedback": "Вы нашли ошибок на 15 из 15 баллов"
}

7. Прохождение открытого задания
bash
curl -X POST http://localhost:8888/api/tasks/77777777-7777-7777-7777-777777777777/submit \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{"answer":"Как пользователь, я хочу сбросить пароль, чтобы восстановить доступ"}'
Ответ:

json
{
  "success": true,
  "pointsAwarded": 20,
  "feedback": "Автоматическая проверка: 20 из 20 баллов"
}

8. Проверка прогресса пользователя
bash
curl http://localhost:8888/api/profile \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000"
Ответ:

json
{
  "success": true,
  "progress": {
    "totalPoints": 45,
    "tasksCompleted": 3,
    "testPoints": 20,
    "errorSpottingPoints": 15,
    "openTasksPoints": 10,
    "lastActivity": "2026-05-10 15:10:15.559582"
  }
}

-----

📊 Типы заданий
1. Тестовые задания (TEST)
Характеристики:

Один или несколько правильных ответов

Автоматическая проверка

Максимальный балл: 5-10

Формат ответа:

json
{"answer": "id1,id2,id3"}
Пример задания:

Вопрос: Какие из перечисленных требований относятся к НЕФУНКЦИОНАЛЬНЫМ?

Варианты:

Скорость загрузки страницы не более 2 секунд ✅

Кнопка должна быть синего цвета ❌

Система должна поддерживать 1000 одновременных пользователей ✅

Пользователь вводит логин и пароль ❌

2. Задания на поиск ошибок (ERROR_SPOTTING)
Характеристики:

Поиск всех ошибок в предоставленном артефакте

Сравнение с эталонным списком

Максимальный балл: 10-15

Формат ответа:

json
{"answer": "ошибка1, ошибка2, ошибка3"}
Пример задания:

Текст BRD:

Система должна быть быстрой.

Пользователь должен иметь возможность оформить заказ.

Админ может удалить любой заказ.

Цвет корзины должен быть зелёным.

Ожидаемые ошибки:

Нет критериев измеримости для "быстрой"

Не указано, кто может оформлять заказ

Нет обоснования удаления заказа админом

Цвет корзины - это дизайн, не функциональное требование

3. Открытые задания (OPEN)
Характеристики:

Свободный ответ с ключевыми словами

Автоматическая проверка по ключевым словам

Максимальный балл: 20-30

Формат ответа:

json
{"answer": "текст ответа пользователя"}
Пример задания:

Составьте user story для функции "Сброс пароля" в формате:
Как <роль>, я хочу <действие>, чтобы <цель>

Ключевые слова: пользователь, сбросить пароль, восстановить доступ, безопасно

-----

🗄️ Структура базы данных
Схема данных
sql
-- Пользователи
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Задания
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- TEST, ERROR_SPOTTING, OPEN
    max_points INTEGER NOT NULL,
    config JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Варианты ответов для тестов
CREATE TABLE task_answers (
    id UUID PRIMARY KEY,
    task_id UUID REFERENCES tasks(id),
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0
);

-- Эталонные ошибки
CREATE TABLE error_spottings (
    task_id UUID PRIMARY KEY REFERENCES tasks(id),
    expected_errors JSONB NOT NULL,
    error_explanations JSONB
);

-- Открытые задания
CREATE TABLE open_tasks (
    task_id UUID PRIMARY KEY REFERENCES tasks(id),
    keywords TEXT[],
    requires_manual_review BOOLEAN DEFAULT TRUE,
    sample_solution TEXT
);

-- Результаты выполнения
CREATE TABLE user_results (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    task_id UUID REFERENCES tasks(id),
    status VARCHAR(50) DEFAULT 'COMPLETED',
    points_awarded INTEGER DEFAULT 0,
    user_answer TEXT NOT NULL,
    feedback TEXT,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, task_id)
);

-- Прогресс пользователя
CREATE TABLE user_progress (
    user_id UUID PRIMARY KEY REFERENCES users(id),
    total_points INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    test_points INTEGER DEFAULT 0,
    error_spotting_points INTEGER DEFAULT 0,
    open_tasks_points INTEGER DEFAULT 0,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-----

🏗️ Архитектура проекта
text
src/main/java/com/trainer/
├── Launcher.java                    # Точка входа, настройка HTTP сервера
│
├── dao/                             # Data Access Objects
│   ├── DatabaseConnection.java      # Подключение к PostgreSQL
│   ├── TaskDao.java                 # Работа с заданиями
│   ├── UserDao.java                 # Работа с пользователями
│   └── UserResultDao.java           # Работа с результатами
│
├── model/                           # Entity классы
│   ├── Task.java                    # Задание
│   ├── TaskAnswer.java              # Вариант ответа
│   ├── User.java                    # Пользователь
│   └── UserResult.java              # Результат выполнения
│
├── service/                         # Бизнес-логика
│   ├── TaskSubmissionService.java   # Проверка ответов, начисление баллов
│   └── UserService.java             # Регистрация, аутентификация
│
├── server/handlers/                 # HTTP обработчики
│   ├── GetTasksHandler.java         # GET /api/tasks
│   ├── LoginHandler.java            # POST /api/login
│   ├── ProfileHandler.java          # GET /api/profile
│   ├── RegisterHandler.java         # POST /api/register
│   └── SubmitTaskHandler.java       # POST /api/tasks/{id}/submit
│
└── util/                            # Утилиты
    ├── JsonUtil.java                # Работа с JSON (Gson)
    └── PasswordHasher.java          # Хэширование паролей (SHA-256)

🐳 Docker конфигурация

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY src/ /app/src/
COPY lib/ /app/lib/
COPY init.sql /app/

RUN mkdir -p /app/out

RUN find /app/src -name "*.java" > sources.txt && \
    javac -cp "/app/lib/*" -d /app/out @sources.txt

WORKDIR /app/out

EXPOSE 8888

CMD ["java", "-cp", ".:/app/lib/*", "com.trainer.Launcher"]
docker-compose.yml
yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: trainer-postgres
    environment:
      POSTGRES_DB: trainer_db
      POSTGRES_USER: trainer
      POSTGRES_PASSWORD: trainer123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U trainer"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - trainer-network

  app:
    build: .
    container_name: trainer-app
    ports:
      - "8888:8888"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/trainer_db
      DB_USER: trainer
      DB_PASSWORD: trainer123
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - trainer-network
    restart: unless-stopped

volumes:
  postgres_data:

networks:
  trainer-network:
    driver: bridge

-----

🧪 Тестовые данные
Задания (9 штук)
ID	Название	Тип	Баллы
11111111-1111-1111-1111-111111111111	Нефункциональные требования	TEST	10
22222222-2222-2222-2222-222222222222	UML диаграммы бизнес-процессов	TEST	10
33333333-3333-3333-3333-333333333333	Что такое MTTR?	TEST	5
44444444-4444-4444-4444-444444444444	Поиск ошибок в BRD	ERROR_SPOTTING	15
55555555-5555-5555-5555-555555555555	Поиск ошибок в SQL-запросе	ERROR_SPOTTING	10
66666666-6666-6666-6666-666666666666	Ошибки в Use Case "Сброс пароля"	ERROR_SPOTTING	12
77777777-7777-7777-7777-777777777777	Составление User Story	OPEN	20
88888888-8888-8888-8888-888888888888	Вопросы для сбора требований	OPEN	25
99999999-9999-9999-9999-999999999999	Метрики качества данных в CRM	OPEN	30
Тестовый пользователь
Email	Пароль	Роль
test@trainer.com	test123	STUDENT
📁 Структура репозитория
text
trainer-backend/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── trainer/
│                   ├── Launcher.java
│                   ├── dao/
│                   ├── model/
│                   ├── service/
│                   ├── server/handlers/
│                   └── util/
├── lib/
│   ├── postgresql-42.6.0.jar
│   └── gson-2.10.1.jar
├── init.sql
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── README.md
└── .gitignore

-----

🔧 Возможные ошибки и их решение
Ошибка	Решение
Connection refused:	Убедитесь, что PostgreSQL запущен
Database does not exist:	Выполните init.sql для создания БД
Relation "tasks" does not exist:	Запустите init.sql
Port 8888 already in use:	Измените порт в Launcher.java или завершите процесс
ClassNotFoundException:	Проверьте наличие JAR файлов в папке lib
Access denied for user:	Проверьте логин и пароль в DatabaseConnection.java