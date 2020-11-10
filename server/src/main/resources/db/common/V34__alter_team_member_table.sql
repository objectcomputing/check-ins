ALTER TABLE team_member
ADD COLUMN supervisorid varchar REFERENCES member_profile(id);