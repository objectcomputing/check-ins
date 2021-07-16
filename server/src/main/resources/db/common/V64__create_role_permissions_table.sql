drop table if exists role_permissions;
CREATE TABLE role_permissions (
   id varchar PRIMARY KEY,
   roleId varchar REFERENCES role.id,
   permission varchar
);