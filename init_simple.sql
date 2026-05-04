-- Создание таблиц
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    max_points INTEGER NOT NULL,
    config JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS error_spottings (
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE,
    expected_errors JSONB NOT NULL,
    error_explanations JSONB
);

CREATE TABLE IF NOT EXISTS user_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    points_awarded INTEGER DEFAULT 0,
    user_answer TEXT NOT NULL,
    feedback TEXT,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, task_id)
);

CREATE TABLE IF NOT EXISTS user_progress (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_points INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    test_points INTEGER DEFAULT 0,
    error_spotting_points INTEGER DEFAULT 0,
    open_tasks_points INTEGER DEFAULT 0,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Добавляем только одно задание для теста
INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('44444444-4444-4444-4444-444444444444', 'Find errors in BRD', 'Find all errors.', 'ERROR_SPOTTING', 15, '{}'::jsonb);

INSERT INTO error_spottings (task_id, expected_errors, error_explanations) VALUES
('44444444-4444-4444-4444-444444444444', '["no measurable criteria", "who can place order not specified"]'::jsonb, '{}'::jsonb);

-- Пользователь для подключения из Java
CREATE USER ilya WITH PASSWORD 'ilya123';
GRANT ALL PRIVILEGES ON DATABASE train_db TO ilya;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ilya;
ALTER USER ilya WITH SUPERUSER;
