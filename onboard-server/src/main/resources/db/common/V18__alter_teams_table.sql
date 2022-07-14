ALTER TABLE team
ADD CONSTRAINT team_uniqueconstraint UNIQUE (name);

ALTER TABLE team_member
RENAME COLUMN uuid TO id; 

ALTER TABLE team_member
RENAME COLUMN isLead TO lead;

ALTER TABLE team_member
ADD CONSTRAINT team_member_uniqueconstraint UNIQUE (teamid, memberid);
