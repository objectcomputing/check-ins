ALTER TABLE member_profile
ADD COLUMN supervisorid varchar REFERENCES member_profile(id);