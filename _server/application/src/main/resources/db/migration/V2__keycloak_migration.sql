-- V2: Keycloak-Integration
-- Passwörter werden ab sofort von Keycloak verwaltet.
-- password_hash und login-tracking Felder werden nicht mehr vom Backend befüllt.

-- Passwort-Hash optinal machen (für Keycloak-User wird kein Hash gespeichert)
ALTER TABLE app_users ALTER COLUMN password_hash DROP NOT NULL;

-- Bestehende Zeilen: Keycloak-UUID wird nach erfolgreicher Migration als user_id gesetzt.
-- (Manuelle Datenmigration erforderlich für Produktionsdaten)
