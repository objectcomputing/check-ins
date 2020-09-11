drop type if exists role_t;
create type role_t as enum('admin', 'pdl', 'member');

drop table if exists role;
CREATE TABLE role(
   id varchar PRIMARY KEY,
   role role_t,
   memberid varchar REFERENCES member_profile(uuid),
   UNIQUE(memberid, role)
);
