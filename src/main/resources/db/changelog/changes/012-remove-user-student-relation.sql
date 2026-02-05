-- Drop direct student relation from users; relationship is now via student_join_codes
ALTER TABLE "users" DROP COLUMN IF EXISTS "student_id";
