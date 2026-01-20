--liquibase formatted sql

--changeset codex:001-create-core-schema
CREATE TABLE students (
  id uuid PRIMARY KEY,
  number varchar(64) NOT NULL,
  class_id uuid
);

CREATE TABLE schools (
  id uuid PRIMARY KEY,
  name varchar(255) UNIQUE NOT NULL,
  address varchar(255) NOT NULL
);

CREATE TABLE classes (
  id uuid PRIMARY KEY,
  name varchar(255) NOT NULL,
  start_year integer NOT NULL,
  description text,
  school_id uuid NOT NULL
);

CREATE TABLE comments_associations (
  id uuid PRIMARY KEY,
  entity_id uuid NOT NULL,
  comment_id uuid NOT NULL,
  comment_associate varchar(64) NOT NULL
);

CREATE TABLE comments (
  id uuid PRIMARY KEY,
  created_at timestamp NOT NULL DEFAULT now(),
  user_id uuid NOT NULL,
  content text,
  visible boolean NOT NULL DEFAULT true
);

CREATE TABLE users (
  id uuid PRIMARY KEY,
  student_id uuid NOT NULL,
  name varchar(255),
  email varchar(255) UNIQUE NOT NULL,
  phone varchar(32) UNIQUE NOT NULL,
  password varchar(255) NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  expired boolean NOT NULL DEFAULT false,
  deleted boolean NOT NULL DEFAULT false,
  dek bytea
);

CREATE TABLE contributions (
  id uuid PRIMARY KEY,
  value numeric NOT NULL,
  student_id uuid NOT NULL,
  target_id uuid NOT NULL,
  created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE targets (
  id uuid PRIMARY KEY,
  description text,
  summary text,
  due_to date NOT NULL,
  estimated_value numeric NOT NULL,
  created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE polls_questions (
  id uuid PRIMARY KEY,
  question text NOT NULL,
  poll_id uuid NOT NULL,
  created_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE polls_answers (
  id uuid PRIMARY KEY,
  poll_question_id uuid NOT NULL,
  created_at timestamp NOT NULL DEFAULT now(),
  student_id uuid NOT NULL,
  user_id uuid NOT NULL
);

CREATE TABLE notifications (
  id uuid PRIMARY KEY,
  summary text,
  created_at timestamp NOT NULL DEFAULT now(),
  user_id uuid NOT NULL,
  is_read boolean NOT NULL DEFAULT false
);

CREATE TABLE announcements (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL,
  is_read boolean NOT NULL DEFAULT false,
  created_at timestamp NOT NULL DEFAULT now(),
  description text,
  summary text,
  published_at timestamp NOT NULL DEFAULT now()
);

CREATE TABLE roles (
  role varchar(64) PRIMARY KEY
);

CREATE TABLE user_roles (
  role varchar(64) NOT NULL,
  user_id uuid NOT NULL,
  PRIMARY KEY (role, user_id)
);

CREATE TABLE payments (
  id uuid PRIMARY KEY,
  date timestamp NOT NULL DEFAULT now(),
  user_id uuid NOT NULL,
  contribution_id uuid NOT NULL,
  value numeric NOT NULL,
  is_success boolean NOT NULL,
  rejected boolean NOT NULL,
  returned boolean NOT NULL,
  vendor_id varchar(255) NOT NULL,
  payment_type varchar(64) NOT NULL
);

COMMENT ON COLUMN comments_associations.comment_associate IS 'enum type';
COMMENT ON COLUMN payments.payment_type IS 'blik or sth else';

ALTER TABLE students
  ADD CONSTRAINT fk_students_class
  FOREIGN KEY (class_id) REFERENCES classes (id);

ALTER TABLE users
  ADD CONSTRAINT fk_users_student
  FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE payments
  ADD CONSTRAINT fk_payments_user
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE classes
  ADD CONSTRAINT fk_classes_school
  FOREIGN KEY (school_id) REFERENCES schools (id);

ALTER TABLE payments
  ADD CONSTRAINT fk_payments_contribution
  FOREIGN KEY (contribution_id) REFERENCES contributions (id);

ALTER TABLE contributions
  ADD CONSTRAINT fk_contributions_student
  FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE contributions
  ADD CONSTRAINT fk_contributions_target
  FOREIGN KEY (target_id) REFERENCES targets (id);

ALTER TABLE notifications
  ADD CONSTRAINT fk_notifications_user
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE announcements
  ADD CONSTRAINT fk_announcements_user
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE polls_answers
  ADD CONSTRAINT fk_polls_answers_question
  FOREIGN KEY (poll_question_id) REFERENCES polls_questions (id);

ALTER TABLE user_roles
  ADD CONSTRAINT fk_user_roles_role
  FOREIGN KEY (role) REFERENCES roles (role);

ALTER TABLE user_roles
  ADD CONSTRAINT fk_user_roles_user
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE polls_answers
  ADD CONSTRAINT fk_polls_answers_user
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE polls_answers
  ADD CONSTRAINT fk_polls_answers_student
  FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE comments_associations
  ADD CONSTRAINT fk_comments_associations_comment
  FOREIGN KEY (comment_id) REFERENCES comments (id);
