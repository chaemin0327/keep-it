ALTER TABLE keepit.user_profile
    ADD COLUMN IF NOT EXISTS school varchar(120),
    ADD COLUMN IF NOT EXISTS education_level varchar(30),
    ADD COLUMN IF NOT EXISTS enrollment_status varchar(30),
    ADD COLUMN IF NOT EXISTS major varchar(120);
