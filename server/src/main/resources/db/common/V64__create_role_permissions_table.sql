drop type if exists permission_t;
create type permission_t as enum('admin', 'pdl', 'member');

drop table if exists role_permissions;
CREATE TABLE role_permissions(
   id varchar PRIMARY KEY,
   permission permission_t,
   memberid varchar REFERENCES member_profile(id),
   UNIQUE(memberid, permission)
);

--drop type if exists role_t;
--create type role_t as enum('admin', 'pdl', 'member');
--
--drop table if exists role;
--CREATE TABLE role(
--   id varchar PRIMARY KEY,
--   role role_t,
--   memberid varchar REFERENCES member_profile(uuid),
--   UNIQUE(memberid, role)
--);
--
--
--ALTER TABLE role
--ALTER COLUMN role TYPE VARCHAR;
--
--ALTER TABLE role
--ADD CONSTRAINT available_roles CHECK (role IN ('ADMIN', 'PDL', 'MEMBER') );