-- ============================================
-- Admin Schema: V1 Initial Migration
-- ============================================

-- Schools
CREATE TABLE schools (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    address TEXT,
    contact_email VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_schools_code ON schools(code);
CREATE INDEX idx_schools_active ON schools(is_active);

-- School Classes
CREATE TABLE school_classes (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    school_year VARCHAR(20) NOT NULL,
    school_id UUID NOT NULL REFERENCES schools(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_classes_school ON school_classes(school_id);
CREATE INDEX idx_classes_year ON school_classes(school_year);

-- Class Teachers (Mapping to Keycloak user IDs)
CREATE TABLE class_teachers (
    class_id UUID NOT NULL REFERENCES school_classes(id) ON DELETE CASCADE,
    teacher_user_id UUID NOT NULL,
    PRIMARY KEY (class_id, teacher_user_id)
);

CREATE INDEX idx_class_teachers_teacher ON class_teachers(teacher_user_id);

-- Sample School Data
INSERT INTO schools (id, name, code, address, contact_email)
VALUES
    (gen_random_uuid(), 'HTL Leoben', 'HTL-LEOBEN', 'Max-Tendler-Straße 3, 8700 Leoben', 'office@htl-leoben.at');
