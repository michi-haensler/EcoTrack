-- ============================================
-- Challenge Schema: V1 Initial Migration
-- ============================================

-- Challenges
CREATE TABLE challenges (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    class_id UUID,
    goal_value INT NOT NULL,
    goal_unit VARCHAR(50) NOT NULL,
    reward_points INT NOT NULL DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_challenges_class ON challenges(class_id);
CREATE INDEX idx_challenges_status ON challenges(status);
CREATE INDEX idx_challenges_dates ON challenges(start_date, end_date);

-- Challenge Participations
CREATE TABLE challenge_participations (
    id UUID PRIMARY KEY,
    challenge_id UUID NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    eco_user_id UUID NOT NULL,
    current_value INT NOT NULL DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    UNIQUE(challenge_id, eco_user_id)
);

CREATE INDEX idx_participation_user ON challenge_participations(eco_user_id);
CREATE INDEX idx_participation_challenge ON challenge_participations(challenge_id);
