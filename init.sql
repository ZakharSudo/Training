-- База данных для тренажёра аналитиков 
 
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
    max_points INTEGER NOT NULL CHECK (max_points > 0), 
    config JSONB, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
); 
 
CREATE TABLE IF NOT EXISTS task_answers ( 
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE, 
    answer_text TEXT NOT NULL, 
    is_correct BOOLEAN NOT NULL DEFAULT FALSE, 
    sort_order INTEGER DEFAULT 0 
); 
 
CREATE TABLE IF NOT EXISTS error_spottings ( 
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE, 
    expected_errors JSONB NOT NULL, 
    error_explanations JSONB 
); 
 
CREATE TABLE IF NOT EXISTS open_tasks ( 
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE, 
    keywords TEXT[], 
    requires_manual_review BOOLEAN DEFAULT TRUE, 
    sample_solution TEXT 
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
 
CREATE INDEX IF NOT EXISTS idx_user_results_user_id ON user_results(user_id); 
CREATE INDEX IF NOT EXISTS idx_user_results_task_id ON user_results(task_id); 
 
INSERT INTO tasks (id, title, description, type, max_points, config) VALUES 
('11111111-1111-1111-1111-111111111111', 
 'Нефункциональные требования', 
 'Какие из перечисленных требований относятся к НЕФУНКЦИОНАЛЬНЫМ?', 
 'TEST', 10, '{"check_type": "multiple_correct"}'::jsonb); 
INSERT INTO tasks (id, title, description, type, max_points, config) VALUES 
('22222222-2222-2222-2222-222222222222', 
 'UML диаграммы бизнес-процессов', 
 'Какие диаграммы UML используются для описания бизнес-процессов?', 
 'TEST', 10, '{"check_type": "multiple_correct"}'::jsonb); 
INSERT INTO tasks (id, title, description, type, max_points, config) VALUES 
('33333333-3333-3333-3333-333333333333', 
 'Что такое MTTR?', 
 'Выберите правильное определение метрики MTTR:', 
 'TEST', 5, '{"check_type": "single_correct"}'::jsonb); 
