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


## 📦 ВАРИАНТ 1: ЛОКАЛЬНЫЙ ЗАПУСК (БЕЗ DOCKER)

### Шаг 1: Установка PostgreSQL

1. Скачайте PostgreSQL с официального сайта: https://www.postgresql.org/download/windows/
2. Запустите установщик
3. При установке задайте пароль: **`1`** (или запомните свой)
4. Остальные параметры оставьте по умолчанию (порт 5432)
5. Завершите установку

### Шаг 2: Создание базы данных

**Откройте командную строку (cmd)** и выполните:

```cmd
cd /d "C:\Program Files\PostgreSQL\17\bin"
set PGPASSWORD=1

createdb.exe -U postgres -h localhost trainer_db

psql.exe -U postgres -h localhost -c "CREATE USER trainer WITH PASSWORD 'trainer123';"
psql.exe -U postgres -h localhost -c "GRANT ALL PRIVILEGES ON DATABASE trainer_db TO trainer;"
psql.exe -U postgres -h localhost -d trainer_db -c "GRANT ALL PRIVILEGES ON SCHEMA public TO trainer;"
psql.exe -U postgres -h localhost -d trainer_db -c "ALTER SCHEMA public OWNER TO trainer;"

set PGPASSWORD=
Примечание: Если PostgreSQL версии 16, замените 17 на 16 в путях. Путь может отличаться.
ПРИМЕЧАНИЕ: ПУТЬ ДИРРЕКТОРИИ ПРОЕКТА НУЖНО УКАЗАТЬ СВОЙ.

Шаг 3: Инициализация таблиц
cmd
cd /d "C:\Program Files\PostgreSQL\17\bin"
set PGPASSWORD=1
psql.exe -U postgres -h localhost -d trainer_db -f "C:\Users\Zakhar\Desktop\Training\init.sql"
set PGPASSWORD=

Шаг 4: Скачивание зависимостей
cmd
cd /d C:\Users\Zakhar\Desktop\Training
mkdir lib

# Скачиваем PostgreSQL JDBC драйвер
powershell -Command "Invoke-WebRequest -Uri 'https://jdbc.postgresql.org/download/postgresql-42.6.0.jar' -OutFile 'lib\postgresql-42.6.0.jar'"

# Скачиваем Gson
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar' -OutFile 'lib\gson-2.10.1.jar'"

Шаг 5: Компиляция проекта
cmd
cd /d C:\Users\Zakhar\Desktop\Training

# Создаём список всех Java файлов
dir /s /B src\main\java\*.java > sources.txt

# Компилируем
javac -d out -cp "lib\*" @sources.txt
Если компиляция успешна — вы увидите пустую строку (без ошибок).

Шаг 6: Запуск сервера
cmd
cd /d C:\Users\Zakhar\Desktop\Training
java -cp "out;lib\*" com.trainer.Launcher
После запуска вы увидите:

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
Важно: Оставьте это окно открытым! Сервер должен работать постоянно.

Шаг 7: Тестирование API (в новом окне cmd)
Откройте НОВОЕ окно командной строки (не закрывая окно с сервером) и выполните:

cmd
# Проверка работоспособности
curl http://localhost:8888/ping

# Получение списка заданий
curl http://localhost:8888/api/tasks

# Регистрация пользователя
curl -X POST http://localhost:8888/api/register -H "Content-Type: application/json" -d "{\"email\":\"student@test.com\",\"password\":\"123456\",\"fullName\":\"Иван Иванов\"}"

# Вход в систему (скопируйте userId из ответа)
curl -X POST http://localhost:8888/api/login -H "Content-Type: application/json" -d "{\"email\":\"student@test.com\",\"password\":\"123456\"}"

# Проверка прогресса (подставьте реальный userId)
curl http://localhost:8888/api/profile -H "X-User-Id: ВАШ_USER_ID_ИЗ_ОТВЕТА"

# Прохождение тестового задания
curl -X POST http://localhost:8888/api/tasks/11111111-1111-1111-1111-111111111111/submit -H "Content-Type: application/json" -H "X-User-Id: ВАШ_USER_ID" -d "{\"answer\":\"a1111111-1111-1111-1111-111111111111,a3111111-1111-1111-1111-111111111111\"}"

🐳 ВАРИАНТ 2: ЗАПУСК ЧЕРЕЗ DOCKER
Требования
Установленный Docker Desktop

Включенная виртуализация в BIOS

Шаг 1: Установка Docker Desktop
Скачайте Docker Desktop с официального сайта: https://www.docker.com/products/docker-desktop/

Установите, выбрав опцию "Use WSL 2 instead of Hyper-V"

Перезагрузите компьютер

Запустите Docker Desktop (иконка кита в трее)

Шаг 2: Создание Dockerfile
Создайте файл Dockerfile в папке проекта:

dockerfile
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
Шаг 3: Создание docker-compose.yml
Создайте файл docker-compose.yml в папке проекта:

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
Шаг 4: Создание .dockerignore
Создайте файл .dockerignore в папке проекта:

text
.git
.gitignore
README.md
.idea/
*.iml
*.log
out/
target/
build/
Шаг 5: Запуск контейнеров
Откройте командную строку (cmd) и выполните:

cmd
cd /d C:\Users\Zakhar\Desktop\Training
docker-compose up -d
Что произойдёт:

Docker скачает образ PostgreSQL (если его нет)

Docker соберёт образ приложения

Запустятся два контейнера: trainer-postgres и trainer-app

Шаг 6: Проверка работы контейнеров
cmd
# Просмотр запущенных контейнеров
docker ps

# Должны увидеть:
# trainer-postgres (статус: healthy)
# trainer-app (статус: up)
Шаг 7: Тестирование API
cmd
# Проверка работоспособности
curl http://localhost:8888/ping

# Получение списка заданий
curl http://localhost:8888/api/tasks

# Регистрация пользователя
curl -X POST http://localhost:8888/api/register -H "Content-Type: application/json" -d "{\"email\":\"docker@test.com\",\"password\":\"123456\",\"fullName\":\"Docker User\"}"
Полезные команды Docker
cmd
# Просмотр логов
docker-compose logs -f

# Просмотр логов только приложения
docker-compose logs app

# Просмотр логов только базы данных
docker-compose logs postgres

# Остановка контейнеров
docker-compose down

# Остановка с удалением томов (очистка БД)
docker-compose down -v

# Перезапуск контейнеров
docker-compose restart

# Пересборка образа без кэша
docker-compose build --no-cache
📡 API Эндпоинты
Метод	Эндпоинт	Описание	Заголовки
GET	/ping	Проверка работоспособности	-
POST	/api/register	Регистрация пользователя	Content-Type: application/json
POST	/api/login	Вход в систему	Content-Type: application/json
GET	/api/tasks	Получение списка заданий	-
POST	/api/tasks/{id}/submit	Отправка ответа на задание	Content-Type: application/json, X-User-Id
GET	/api/profile	Получение прогресса пользователя	X-User-Id
🔧 Примеры запросов
1. Проверка работоспособности
cmd
curl http://localhost:8888/ping
Ответ:

json
{"pong":true}
2. Регистрация пользователя
cmd
curl -X POST http://localhost:8888/api/register -H "Content-Type: application/json" -d "{\"email\":\"student@test.com\",\"password\":\"123456\",\"fullName\":\"Иван Иванов\"}"
Ответ:

json
{
  "success": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "student@test.com",
  "role": "STUDENT"
}
3. Вход в систему
cmd
curl -X POST http://localhost:8888/api/login -H "Content-Type: application/json" -d "{\"email\":\"student@test.com\",\"password\":\"123456\"}"
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
cmd
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
    }
  ]
}
5. Прохождение тестового задания
cmd
curl -X POST http://localhost:8888/api/tasks/11111111-1111-1111-1111-111111111111/submit -H "Content-Type: application/json" -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" -d "{\"answer\":\"a1111111-1111-1111-1111-111111111111,a3111111-1111-1111-1111-111111111111\"}"
Ответ:

json
{
  "success": true,
  "pointsAwarded": 10,
  "feedback": "Вы получили 10 из 10 баллов"
}
6. Проверка прогресса пользователя
cmd
curl http://localhost:8888/api/profile -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000"
Ответ:

json
{
  "success": true,
  "progress": {
    "totalPoints": 10,
    "tasksCompleted": 1,
    "testPoints": 10,
    "errorSpottingPoints": 0,
    "openTasksPoints": 0,
    "lastActivity": "2026-05-10 15:10:15.559582"
  }
}
📊 Типы заданий
1. Тестовые задания (TEST)
Один или несколько правильных ответов

Автоматическая проверка

Максимальный балл: 5-10

Формат ответа: {"answer": "id1,id2,id3"}

2. Задания на поиск ошибок (ERROR_SPOTTING)
Поиск всех ошибок в предоставленном артефакте

Сравнение с эталонным списком

Максимальный балл: 10-15

Формат ответа: {"answer": "ошибка1, ошибка2, ошибка3"}

3. Открытые задания (OPEN)
Свободный ответ с ключевыми словами

Автоматическая проверка по ключевым словам

Максимальный балл: 20-30

Формат ответа: {"answer": "текст ответа пользователя"}

🗄️ Структура базы данных
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
    type VARCHAR(50) NOT NULL,
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
📈 Система начисления баллов
Тип задания	Формула
TEST	(правильные_ответы / всего_ответов) × max_points
ERROR_SPOTTING	(найденные_ошибки / всего_ошибок) × max_points
OPEN	(найденные_ключевые_слова / всего_ключевых_слов) × max_points
🧪 Тестовые данные
Задания (9 штук)
ID	Название	Тип	Баллы
11111111-1111-1111-1111-111111111111	Нефункциональные требования	TEST	10
22222222-2222-2222-2222-222222222222	UML диаграммы бизнес-процессов	TEST	10
33333333-3333-3333-3333-333333333333	Что такое MTTR?	TEST	5
44444444-4444-4444-4444-444444444444	Поиск ошибок в BRD	ERROR_SPOTTING	15
55555555-5555-5555-5555-555555555555	Поиск ошибок в SQL-запросе	ERROR_SPOTTING	10
66666666-6666-6666-6666-666666666666	Ошибки в Use Case	ERROR_SPOTTING	12
77777777-7777-7777-7777-777777777777	Составление User Story	OPEN	20
88888888-8888-8888-8888-888888888888	Вопросы для сбора требований	OPEN	25
99999999-9999-9999-9999-999999999999	Метрики качества данных	OPEN	30