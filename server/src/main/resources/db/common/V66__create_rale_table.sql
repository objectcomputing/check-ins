DROP TABLE IF EXISTS rale;
CREATE TABLE rale (
  id varchar PRIMARY KEY,
  rale varchar,
  description varchar,
  CONSTRAINT rale_uniqueconstraint UNIQUE (rale)
);

DROP TABLE IF EXISTS rale_member;
CREATE TABLE rale_member (
  id varchar PRIMARY KEY,
  raleId varchar REFERENCES rale ON DELETE CASCADE,
  memberId varchar REFERENCES member_profile,
  lead boolean default false,
  CONSTRAINT rale_member_uniqueconstraint UNIQUE (raleid, memberid)
);

--ALTER TABLE rale_member
--DROP CONSTRAINT rale_member_raleid_fkey;
--
--ALTER TABLE rale_member
--ADD CONSTRAINT rale_member_raleid_fkey FOREIGN KEY (raleId) REFERENCES rale(id) ON DELETE CASCADE;