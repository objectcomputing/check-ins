ALTER TABLE role ALTER COLUMN role TYPE varchar;

create unique index case_insensitive_role_name on role (lower(role));

ALTER TABLE role DROP CONSTRAINT available_roles;