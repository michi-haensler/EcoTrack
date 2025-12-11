-- ============================================
-- Scoring Schema: V1 Initial Migration
-- ============================================

-- Action Definitions (Katalog der möglichen Aktionen)
CREATE TABLE action_definitions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    co2_saved_grams INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Activity Entries (Geloggte Aktivitäten)
CREATE TABLE activity_entries (
    id UUID PRIMARY KEY,
    eco_user_id UUID NOT NULL,
    action_definition_id UUID NOT NULL REFERENCES action_definitions(id),
    quantity INT NOT NULL DEFAULT 1,
    total_points INT NOT NULL,
    source VARCHAR(50) NOT NULL,
    notes TEXT,
    logged_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activity_entries_user ON activity_entries(eco_user_id);
CREATE INDEX idx_activity_entries_logged_at ON activity_entries(logged_at);

-- Points Ledger (Punkte-Historie)
CREATE TABLE points_ledger_entries (
    id UUID PRIMARY KEY,
    eco_user_id UUID NOT NULL,
    activity_entry_id UUID REFERENCES activity_entries(id),
    points INT NOT NULL,
    reason VARCHAR(200) NOT NULL,
    balance_after BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_points_ledger_user ON points_ledger_entries(eco_user_id);

-- Initial Action Catalog
INSERT INTO action_definitions (id, name, description, points, category, unit, co2_saved_grams, is_active)
VALUES
    (gen_random_uuid(), 'Fahrrad fahren', 'Statt Auto mit dem Fahrrad gefahren', 15, 'MOBILITY', 'KILOMETER', 150, true),
    (gen_random_uuid(), 'Öffentliche Verkehrsmittel', 'Bus oder Bahn statt Auto benutzt', 10, 'MOBILITY', 'KILOMETER', 100, true),
    (gen_random_uuid(), 'Zu Fuß gehen', 'Strecke zu Fuß zurückgelegt', 5, 'MOBILITY', 'KILOMETER', 120, true),
    (gen_random_uuid(), 'Vegetarisches Essen', 'Vegetarisches Gericht gewählt', 8, 'NUTRITION', 'PORTION', 500, true),
    (gen_random_uuid(), 'Veganes Essen', 'Veganes Gericht gewählt', 12, 'NUTRITION', 'PORTION', 800, true),
    (gen_random_uuid(), 'Regionales Produkt', 'Regionales/Saisonales Produkt gekauft', 5, 'NUTRITION', 'ITEM', 200, true),
    (gen_random_uuid(), 'Müll getrennt', 'Müll korrekt getrennt', 3, 'WASTE', 'ACTION', 50, true),
    (gen_random_uuid(), 'Plastik vermieden', 'Plastikverpackung vermieden', 5, 'WASTE', 'ITEM', 100, true),
    (gen_random_uuid(), 'Wiederverwendbar', 'Mehrwegprodukt statt Einweg verwendet', 4, 'WASTE', 'ACTION', 80, true),
    (gen_random_uuid(), 'Strom gespart', 'Elektrogerät ausgeschaltet statt Standby', 2, 'ENERGY', 'ACTION', 30, true),
    (gen_random_uuid(), 'Wasser gespart', 'Wasser beim Duschen/Zähneputzen gespart', 3, 'WATER', 'ACTION', 20, true),
    (gen_random_uuid(), 'Second Hand', 'Gebrauchten Artikel gekauft statt neu', 10, 'CONSUMPTION', 'ITEM', 1000, true);
