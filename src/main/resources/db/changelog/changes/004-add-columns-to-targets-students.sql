ALTER TABLE target_students
  ADD COLUMN fee_amount numeric(38,2),
  ADD COLUMN fee_calculated_at timestamp without time zone;
