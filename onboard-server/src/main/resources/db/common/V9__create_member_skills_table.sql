drop table if exists member_skills;
CREATE TABLE member_skills(
   id varchar PRIMARY KEY,
   memberid varchar REFERENCES member_profile(uuid),
   skillid varchar REFERENCES skills(skillid)
);
