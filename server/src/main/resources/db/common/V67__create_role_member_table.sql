DROP TABLE IF EXISTS role_member;
CREATE TABLE role_member (
  id varchar PRIMARY KEY,
  roleId varchar REFERENCES role ON DELETE CASCADE,
  memberId varchar REFERENCES member_profile,
  CONSTRAINT role_member_uniqueconstraint UNIQUE (roleid, memberid)
);