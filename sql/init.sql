-- ============================================
-- БАЗА ДАННЫХ ДЛЯ ТРЕНАЖЁРА АНАЛИТИКОВ
-- ============================================

-- Переключаемся на базу trainer_db (создадим позже)
-- Пока просто подключаемся

-- ============================================
-- 1. ПОЛЬЗОВАТЕЛИ
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- ============================================
-- 2. ЗАДАНИЯ
-- ============================================
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    max_points INTEGER NOT NULL CHECK (max_points > 0),
    config JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 3. ВАРИАНТЫ ОТВЕТОВ ДЛЯ ТЕСТОВ
-- ============================================
CREATE TABLE IF NOT EXISTS task_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0
);

-- ============================================
-- 4. ЭТАЛОННЫЕ ОШИБКИ
-- ============================================
CREATE TABLE IF NOT EXISTS error_spottings (
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE,
    expected_errors JSONB NOT NULL,
    error_explanations JSONB
);

-- ============================================
-- 5. ОТКРЫТЫЕ ЗАДАНИЯ
-- ============================================
CREATE TABLE IF NOT EXISTS open_tasks (
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE,
    keywords TEXT[],
    requires_manual_review BOOLEAN DEFAULT TRUE,
    sample_solution TEXT
);

-- ============================================
-- 6. РЕЗУЛЬТАТЫ ВЫПОЛНЕНИЯ
-- ============================================
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

-- ============================================
-- 7. ПРОГРЕСС ПОЛЬЗОВАТЕЛЯ
-- ============================================
CREATE TABLE IF NOT EXISTS user_progress (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_points INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    test_points INTEGER DEFAULT 0,
    error_spotting_points INTEGER DEFAULT 0,
    open_tasks_points INTEGER DEFAULT 0,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 8. СЕССИИ
-- ============================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ИНДЕКСЫ
-- ============================================
CREATE INDEX IF NOT EXISTS idx_user_results_user_id ON user_results(user_id);
CREATE INDEX IF NOT EXISTS idx_user_results_task_id ON user_results(task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_type ON tasks(type);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token ON user_sessions(token);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);

-- ============================================
-- ТРИГГЕР ОБНОВЛЕНИЯ АКТИВНОСТИ
-- ============================================
CREATE OR REPLACE FUNCTION update_last_activity()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE user_progress
    SET last_activity = CURRENT_TIMESTAMP
    WHERE user_id = NEW.user_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_activity ON user_results;
CREATE TRIGGER trigger_update_activity
AFTER INSERT ON user_results
FOR EACH ROW
EXECUTE FUNCTION update_last_activity();

-- ============================================
-- ТЕСТОВЫЕ ДАННЫЕ
-- ============================================

-- Добавляем расширение для генерации UUID (если ещё не добавлено)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Очищаем старые данные (если есть)
TRUNCATE tasks CASCADE;

-- --- ТЕСТОВЫЕ ЗАДАНИЯ (3 штуки) ---

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('11111111-1111-1111-1111-111111111111',
 'Нефункциональные требования',
 'Какие из перечисленных требований относятся к НЕФУНКЦИОНАЛЬНЫМ? Выберите все подходящие варианты.',
 'TEST',
 10,
 '{"check_type": "multiple_correct"}'::jsonb);

INSERT INTO task_answers (id, task_id, answer_text, is_correct, sort_order) VALUES
('a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Скорость загрузки страницы не более 2 секунд', TRUE, 1),
('a2111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Кнопка должна быть синего цвета', FALSE, 2),
('a3111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Система должна поддерживать 1000 одновременных пользователей', TRUE, 3),
('a4111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Пользователь вводит логин и пароль', FALSE, 4);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('22222222-2222-2222-2222-222222222222',
 'UML диаграммы бизнес-процессов',
 'Какие диаграммы UML используются для описания бизнес-процессов? Выберите все подходящие варианты.',
 'TEST',
 10,
 '{"check_type": "multiple_correct"}'::jsonb);

INSERT INTO task_answers (id, task_id, answer_text, is_correct, sort_order) VALUES
('b1222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Диаграмма вариантов использования (Use Case)', TRUE, 1),
('b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Диаграмма классов', FALSE, 2),
('b3222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Диаграмма активности (Activity)', TRUE, 3),
('b4222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Диаграмма развёртывания', FALSE, 4);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('33333333-3333-3333-3333-333333333333',
 'Что такое MTTR?',
 'Выберите правильное определение метрики MTTR:',
 'TEST',
 5,
 '{"check_type": "single_correct"}'::jsonb);

INSERT INTO task_answers (id, task_id, answer_text, is_correct, sort_order) VALUES
('c1333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Среднее время восстановления системы', TRUE, 1),
('c2333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Среднее время между сбоями', FALSE, 2),
('c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'Процент успешных запросов', FALSE, 3);

-- --- ЗАДАНИЯ НА ПОИСК ОШИБОК (3 штуки) ---

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('44444444-4444-4444-4444-444444444444',
 'Поиск ошибок в BRD',
 'Ниже приведён фрагмент BRD для интернет-магазина. Найдите ВСЕ ошибки в требованиях и перечислите их (каждую с новой строки).\n\nТекст:\n1. Система должна быть быстрой.\n2. Пользователь должен иметь возможность оформить заказ.\n3. Админ может удалить любой заказ.\n4. Цвет корзины должен быть зелёным.',
 'ERROR_SPOTTING',
 15,
 '{}'::jsonb);

INSERT INTO error_spottings (task_id, expected_errors, error_explanations) VALUES
('44444444-4444-4444-4444-444444444444',
 '["Нет критериев измеримости для \"быстрой\"","Не указано, кто может оформлять заказ (авторизованные/гости)","Нет обоснования удаления заказа админом","Цвет корзины - это дизайн, не функциональное требование"]'::jsonb,
 '{"note": "Требования должны быть SMART: конкретными и измеримыми"}'::jsonb);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('55555555-5555-5555-5555-555555555555',
 'Поиск ошибок в SQL-запросе',
 'Найдите ВСЕ ошибки в следующем SQL-запросе:\n\nSELECT * FROM users WHERE name = null ORDER BY created;',
 'ERROR_SPOTTING',
 10,
 '{}'::jsonb);

INSERT INTO error_spottings (task_id, expected_errors, error_explanations) VALUES
('55555555-5555-5555-5555-555555555555',
 '["Сравнение с NULL через =, надо IS NULL","Поле created не существует (возможно created_at)","ORDER BY без указания направления (ASC/DESC)"]'::jsonb,
 '{"note": "В SQL NULL проверяется через IS NULL или IS NOT NULL"}'::jsonb);

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('66666666-6666-6666-6666-666666666666',
 'Ошибки в Use Case "Сброс пароля"',
 'Проанализируйте use case и найдите ошибки:\n\nАктёр: Пользователь\nБазовый поток:\n1. Пользователь нажимает "Забыли пароль"\n2. Система отправляет новый пароль на email\n3. Пользователь входит с новым паролем',
 'ERROR_SPOTTING',
 12,
 '{}'::jsonb);

INSERT INTO error_spottings (task_id, expected_errors, error_explanations) VALUES
('66666666-6666-6666-6666-666666666666',
 '["Нет альтернативных потоков (email не найден, письмо не доставлено)","Нет проверки личности перед отправкой","Нет требований к сложности нового пароля","Нет истечения срока действия временного пароля"]'::jsonb,
 '{"security": "Use case должен учитывать безопасность"}'::jsonb);

-- --- ОТКРЫТЫЕ ЗАДАНИЯ (3 штуки) ---

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('77777777-7777-7777-7777-777777777777',
 'Составление User Story',
 'Составьте user story для функции "Сброс пароля" в формате:\nКак <роль>, я хочу <действие>, чтобы <цель>\n\nПридумайте 2-3 варианта.',
 'OPEN',
 20,
 '{"auto_check": true, "keywords": ["пользователь", "сбросить пароль", "восстановить доступ"]}'::jsonb);

INSERT INTO open_tasks (task_id, keywords, requires_manual_review, sample_solution) VALUES
('77777777-7777-7777-7777-777777777777',
 ARRAY['пользователь', 'сбросить пароль', 'восстановить доступ', 'безопасно'],
 FALSE,
 'Как зарегистрированный пользователь, я хочу безопасно сбросить забытый пароль через email, чтобы восстановить доступ к аккаунту.');

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('88888888-8888-8888-8888-888888888888',
 'Вопросы для сбора требований',
 'Представьте, что вы аналитик в проекте по разработке системы бронирования отелей. Напишите 5 вопросов, которые вы зададите заказчику для выявления требований.',
 'OPEN',
 25,
 '{"auto_check": true, "keywords": ["бронирование", "отмена", "оплата", "номера", "пользователи"]}'::jsonb);

INSERT INTO open_tasks (task_id, keywords, requires_manual_review, sample_solution) VALUES
('88888888-8888-8888-8888-888888888888',
 ARRAY['бронирование', 'отмена', 'оплата', 'номер', 'гость', 'админ'],
 FALSE,
 '1. Какие способы оплаты должны поддерживаться?\n2. За сколько часов можно отменить бронь без штрафа?\n3. Нужна ли интеграция с внешними системами (Booking.com)?');

INSERT INTO tasks (id, title, description, type, max_points, config) VALUES
('99999999-9999-9999-9999-999999999999',
 'Метрики качества данных в CRM',
 'Перечислите минимум 5 метрик для оценки качества данных в CRM-системе. Для каждой метрики кратко опишите, что она измеряет.',
 'OPEN',
 30,
 '{"auto_check": true, "keywords": ["полнота", "точность", "уникальность", "консистентность", "своевременность"]}'::jsonb);

INSERT INTO open_tasks (task_id, keywords, requires_manual_review, sample_solution) VALUES
('99999999-9999-9999-9999-999999999999',
 ARRAY['полнота', 'точность', 'уникальность', 'консистентность', 'своевременность', 'валидность'],
 FALSE,
 '1. Полнота: % заполненных обязательных полей\n2. Точность: % записей с корректными форматами\n3. Уникальность: дубликаты клиентов');

-- --- ТЕСТОВЫЙ ПОЛЬЗОВАТЕЛЬ ---
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 'test@trainer.com',
 'test123',
 'Тестовый Пользователь',
 'STUDENT');

INSERT INTO user_progress (user_id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- ============================================
-- ПРОВЕРОЧНЫЕ ЗАПРОСЫ (для тестирования)
-- ============================================
-- SELECT type, COUNT(*) FROM tasks GROUP BY type;
-- SELECT COUNT(*) FROM users WHERE email = 'test@trainer.com';