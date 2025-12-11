-- Init script for PostgreSQL
-- Creates schemas for each bounded context

CREATE SCHEMA IF NOT EXISTS scoring;
CREATE SCHEMA IF NOT EXISTS challenge;
CREATE SCHEMA IF NOT EXISTS userprofile;
CREATE SCHEMA IF NOT EXISTS admin;

-- Grant privileges
GRANT ALL PRIVILEGES ON SCHEMA scoring TO ecotrack;
GRANT ALL PRIVILEGES ON SCHEMA challenge TO ecotrack;
GRANT ALL PRIVILEGES ON SCHEMA userprofile TO ecotrack;
GRANT ALL PRIVILEGES ON SCHEMA admin TO ecotrack;
