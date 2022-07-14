ALTER TABLE team_member
DROP CONSTRAINT team_member_teamid_fkey;

ALTER TABLE team_member
ADD CONSTRAINT team_member_teamid_fkey FOREIGN KEY (teamId) REFERENCES team(id) ON DELETE CASCADE;