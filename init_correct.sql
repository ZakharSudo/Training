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

CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_results_user_id ON user_results(user_id);
CREATE INDEX IF NOT EXISTS idx_user_results_task_id ON user_results(task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_type ON tasks(type);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token ON user_sessions(token);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('11111111-1111-1111-1111-111111111111', 'Non-functional requirements', 'Which requirements are NON-FUNCTIONAL? Select all that apply.', 'TEST', 10, '{"check_type": "multiple_correct"}'::jsonb);

INSERT INTO task_answers (id, task_id, answer_text, is_correct, sort_order) VALUES
('a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Page load time less than 2 seconds', TRUE, 1),
('a2111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Button must be blue', FALSE, 2),
('a3111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'System must support 1000 concurrent users', TRUE, 3),
('a4111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'User enters login and password', FALSE, 4);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('44444444-4444-4444-4444-444444444444', 'Find errors in BRD', 'Find all errors in requirements.', 'ERROR_SPOTTING', 15, '{}'::jsonb);

INSERT INTO error_spottings (task_id, expected_errors, error_explanations) VALUES
('44444444-4444-4444-4444-444444444444', '["no measurable criteria", "who can place order not specified", "no reason for deletion", "color is design not functional"]'::jsonb, '{}'::jsonb);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('77777777-7777-7777-7777-777777777777', 'Write User Story', 'Write a user story for password reset.', 'OPEN', 20, '{"auto_check": true, "keywords": ["user", "reset", "password", "access"]}'::jsonb);

CREATE USER ilya WITH PASSWORD 'ilya123';
GRANT ALL PRIVILEGES ON DATABASE train_db TO ilya;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ilya;
ALTER USER ilya WITH SUPERUSER;
