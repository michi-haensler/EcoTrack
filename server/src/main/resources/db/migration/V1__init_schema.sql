-- V1__init_schema.sql
-- Initial database schema for EcoTrack

-- ============================================
-- Identity & Access Context
-- ============================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('SCHUELER', 'LEHRER', 'ADMIN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DISABLED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ============================================
-- Administration Context
-- ============================================

CREATE TABLE schools (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    school_id UUID NOT NULL REFERENCES schools(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(name, school_id)
);

CREATE INDEX idx_classes_school ON classes(school_id);

-- ============================================
-- User Profile Context
-- ============================================

CREATE TABLE eco_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    class_id UUID REFERENCES classes(id),
    total_points BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_eco_users_user ON eco_users(user_id);
CREATE INDEX idx_eco_users_class ON eco_users(class_id);

-- Teacher-Class assignment (many-to-many)
CREATE TABLE class_teachers (
    class_id UUID NOT NULL REFERENCES classes(id),
    user_id UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (class_id, user_id)
);

-- ============================================
-- Scoring & Activity Context
-- ============================================

CREATE TABLE action_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('MOBILITAET', 'KONSUM', 'RECYCLING', 'ENERGIE', 'ERNAEHRUNG', 'SONSTIGES')),
    unit VARCHAR(20) NOT NULL CHECK (unit IN ('STUECK', 'KM', 'MINUTEN', 'KG', 'LITER')),
    base_points INTEGER NOT NULL CHECK (base_points > 0),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_action_definitions_category ON action_definitions(category);
CREATE INDEX idx_action_definitions_active ON action_definitions(active);

CREATE TABLE activity_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eco_user_id UUID NOT NULL REFERENCES eco_users(id),
    action_definition_id UUID NOT NULL REFERENCES action_definitions(id),
    quantity DECIMAL(10, 2) NOT NULL CHECK (quantity > 0),
    points INTEGER NOT NULL CHECK (points > 0),
    source VARCHAR(20) NOT NULL DEFAULT 'APP' CHECK (source IN ('APP', 'WEB', 'IMPORT')),
    activity_date DATE NOT NULL,
    challenge_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_entries_eco_user ON activity_entries(eco_user_id);
CREATE INDEX idx_activity_entries_date ON activity_entries(activity_date);
CREATE INDEX idx_activity_entries_challenge ON activity_entries(challenge_id);

-- ============================================
-- Progress & Gamification Context
-- ============================================

CREATE TABLE level_thresholds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level VARCHAR(20) NOT NULL UNIQUE CHECK (level IN ('SETZLING', 'JUNGBAUM', 'BAUM', 'ALTBAUM')),
    min_points BIGINT NOT NULL,
    sort_order INTEGER NOT NULL
);

CREATE TABLE milestones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    required_points BIGINT NOT NULL,
    badge_asset VARCHAR(255)
);

CREATE TABLE eco_user_milestones (
    eco_user_id UUID NOT NULL REFERENCES eco_users(id),
    milestone_id UUID NOT NULL REFERENCES milestones(id),
    reached_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (eco_user_id, milestone_id)
);

-- ============================================
-- Challenges Context
-- ============================================

CREATE TABLE challenges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal_value DECIMAL(10, 2) NOT NULL CHECK (goal_value > 0),
    goal_unit VARCHAR(20) NOT NULL CHECK (goal_unit IN ('POINTS', 'ACTIONS')),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'CLOSED')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    class_id UUID NOT NULL REFERENCES classes(id),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date >= start_date)
);

CREATE INDEX idx_challenges_class ON challenges(class_id);
CREATE INDEX idx_challenges_status ON challenges(status);
CREATE INDEX idx_challenges_dates ON challenges(start_date, end_date);

-- Add foreign key for activity_entries.challenge_id
ALTER TABLE activity_entries 
ADD CONSTRAINT fk_activity_challenge 
FOREIGN KEY (challenge_id) REFERENCES challenges(id);

-- ============================================
-- Initial seed data
-- ============================================

-- Insert level thresholds
INSERT INTO level_thresholds (id, level, min_points, sort_order) VALUES
    (gen_random_uuid(), 'SETZLING', 0, 1),
    (gen_random_uuid(), 'JUNGBAUM', 200, 2),
    (gen_random_uuid(), 'BAUM', 500, 3),
    (gen_random_uuid(), 'ALTBAUM', 1000, 4);

-- Insert milestones
INSERT INTO milestones (id, name, required_points, badge_asset) VALUES
    (gen_random_uuid(), 'Erste Schritte', 10, 'badge_first_steps'),
    (gen_random_uuid(), 'Umwelt-Anfänger', 50, 'badge_beginner'),
    (gen_random_uuid(), 'Grüner Held', 100, 'badge_green_hero'),
    (gen_random_uuid(), 'Eco-Champion', 250, 'badge_eco_champion'),
    (gen_random_uuid(), 'Nachhaltigkeits-Meister', 500, 'badge_sustainability_master'),
    (gen_random_uuid(), 'Umwelt-Legende', 1000, 'badge_environment_legend');

-- Insert default action definitions
INSERT INTO action_definitions (id, name, description, category, unit, base_points) VALUES
    (gen_random_uuid(), 'Fahrrad statt Auto', 'Mit dem Fahrrad zur Schule gefahren', 'MOBILITAET', 'KM', 5),
    (gen_random_uuid(), 'Öffentliche Verkehrsmittel', 'Bus oder Bahn genutzt', 'MOBILITAET', 'KM', 3),
    (gen_random_uuid(), 'Zu Fuß gegangen', 'Zu Fuß zur Schule gegangen', 'MOBILITAET', 'KM', 4),
    (gen_random_uuid(), 'Mehrwegbecher benutzt', 'Eigenen Becher statt Einwegbecher', 'KONSUM', 'STUECK', 10),
    (gen_random_uuid(), 'Mehrwegtasche benutzt', 'Stofftasche statt Plastiktüte', 'KONSUM', 'STUECK', 5),
    (gen_random_uuid(), 'Papier recycelt', 'Papier in den Papiercontainer', 'RECYCLING', 'KG', 8),
    (gen_random_uuid(), 'Plastik recycelt', 'Plastik korrekt entsorgt', 'RECYCLING', 'KG', 10),
    (gen_random_uuid(), 'Licht ausgeschaltet', 'Unnötiges Licht ausgeschaltet', 'ENERGIE', 'STUECK', 3),
    (gen_random_uuid(), 'Geräte ausgesteckt', 'Standby-Geräte vom Strom getrennt', 'ENERGIE', 'STUECK', 5),
    (gen_random_uuid(), 'Vegetarisches Essen', 'Vegetarische Mahlzeit gewählt', 'ERNAEHRUNG', 'STUECK', 8),
    (gen_random_uuid(), 'Regionales Produkt gekauft', 'Lokale/regionale Lebensmittel gekauft', 'ERNAEHRUNG', 'STUECK', 6),
    (gen_random_uuid(), 'Wasser gespart', 'Bewusst Wasser gespart', 'SONSTIGES', 'LITER', 2);
