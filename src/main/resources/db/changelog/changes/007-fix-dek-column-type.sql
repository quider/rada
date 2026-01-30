-- liquibase formatted sql

-- changeset adrian:007-fix-dek-column-type
-- Ensure dek column is bytea type for storing byte arrays
ALTER TABLE users ALTER COLUMN dek TYPE bytea USING dek::bytea;
