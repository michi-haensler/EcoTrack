-- ============================================
-- UserProfile Schema: V1 Initial Migration
-- ============================================

-- Eco Users (Application-specific user data)
CREATE TABLE eco_users (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    class_id UUID,
    total_points BIGINT NOT NULL DEFAULT 0,
    level VARCHAR(50) NOT NULL DEFAULT 'SETZLING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_eco_users_user ON eco_users(user_id);
CREATE INDEX idx_eco_users_class ON eco_users(class_id);
CREATE INDEX idx_eco_users_points ON eco_users(total_points DESC);

-- Milestones (Badges)
CREATE TABLE milestones (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    required_points BIGINT NOT NULL,
    badge_asset VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- User-Milestone Mapping
CREATE TABLE eco_user_milestones (
    eco_user_id UUID NOT NULL REFERENCES eco_users(id) ON DELETE CASCADE,
    milestone_id UUID NOT NULL REFERENCES milestones(id) ON DELETE CASCADE,
    PRIMARY KEY (eco_user_id, milestone_id)
);

-- Initial Milestones
INSERT INTO milestones (id, name, required_points, badge_asset, description)
VALUES
    (gen_random_uuid(), 'Erste Schritte', 10, 'badge_first_steps.png', 'Deine ersten 10 Punkte!'),
    (gen_random_uuid(), 'Umwelt-Anfänger', 50, 'badge_beginner.png', '50 Punkte erreicht'),
    (gen_random_uuid(), 'Öko-Enthusiast', 100, 'badge_enthusiast.png', '100 Punkte - Du bist auf dem richtigen Weg!'),
    (gen_random_uuid(), 'Grüner Held', 250, 'badge_green_hero.png', '250 Punkte - Ein echter Held der Umwelt!'),
    (gen_random_uuid(), 'Klima-Champion', 500, 'badge_champion.png', '500 Punkte - Klima-Champion Status erreicht!'),
    (gen_random_uuid(), 'Planet-Retter', 1000, 'badge_planet_saver.png', '1000 Punkte - Du rettest den Planeten!');
