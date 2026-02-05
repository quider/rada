-- liquibase formatted sql

-- changeset adrian:011-add-student-name-columns
ALTER TABLE students ADD COLUMN first_name varchar(64);
ALTER TABLE students ADD COLUMN last_name varchar(64);
