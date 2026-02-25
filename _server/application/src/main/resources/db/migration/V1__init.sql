create table if not exists app_users (
    user_id uuid primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    role varchar(30) not null,
    status varchar(30) not null,
    must_change_password boolean not null,
    failed_login_attempts integer not null default 0,
    locked_until timestamp with time zone,
    external_id varchar(255),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists school_classes (
    class_id uuid primary key,
    name varchar(255) not null,
    school_id uuid not null,
    school_name varchar(255) not null,
    created_at timestamp with time zone not null
);

create table if not exists eco_user_profiles (
    eco_user_id uuid primary key,
    user_id uuid not null unique,
    email varchar(255) not null unique,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    display_name varchar(255) not null,
    class_id uuid,
    class_name varchar(255),
    school_id uuid,
    school_name varchar(255),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists refresh_tokens (
    token varchar(120) primary key,
    user_id uuid not null,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null
);

create table if not exists password_reset_tokens (
    token varchar(120) primary key,
    user_id uuid not null,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null
);

create table if not exists action_definitions (
    action_definition_id uuid primary key,
    name varchar(255) not null,
    description varchar(500),
    category varchar(40) not null,
    unit varchar(40) not null,
    base_points integer not null,
    active boolean not null
);

create table if not exists activity_entries (
    activity_entry_id uuid primary key,
    eco_user_id uuid not null,
    action_definition_id uuid not null,
    action_name varchar(255) not null,
    category varchar(40) not null,
    quantity double precision not null,
    unit varchar(40) not null,
    points integer not null,
    timestamp timestamp with time zone not null,
    activity_date date not null,
    source varchar(30) not null
);

create table if not exists points_ledgers (
    eco_user_id uuid primary key,
    total_points integer not null,
    last_updated timestamp with time zone not null,
    version bigint not null default 0
);

create table if not exists challenges (
    challenge_id uuid primary key,
    title varchar(255) not null,
    description varchar(500),
    status varchar(30) not null,
    goal_value double precision not null,
    goal_unit varchar(30) not null,
    start_date date not null,
    end_date date not null,
    class_id uuid not null,
    class_name varchar(255),
    created_by uuid not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

insert into action_definitions(action_definition_id, name, description, category, unit, base_points, active)
values
    ('11111111-1111-1111-1111-111111111111', 'Fahrrad statt Auto', 'Schulweg mit dem Fahrrad', 'MOBILITAET', 'KM', 10, true),
    ('22222222-2222-2222-2222-222222222222', 'Muell getrennt', 'Recycling korrekt durchgefuehrt', 'RECYCLING', 'STUECK', 5, true),
    ('33333333-3333-3333-3333-333333333333', 'Vegetarische Mahlzeit', 'Nachhaltige Ernaehrung', 'ERNAEHRUNG', 'STUECK', 8, true),
    ('44444444-4444-4444-4444-444444444444', 'Strom sparen', 'Licht und Geraete bewusst ausgeschaltet', 'ENERGIE', 'MINUTEN', 2, true)
on conflict do nothing;
