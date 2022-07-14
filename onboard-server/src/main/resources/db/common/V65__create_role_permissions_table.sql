drop table if exists role_permissions;
CREATE TABLE role_permissions(
   id varchar PRIMARY KEY,
   permission VARCHAR,
   roleid varchar REFERENCES role(id),
   CONSTRAINT available_permissions CHECK (permission IN ('READCHECKIN', 'CREATECHECKIN', 'DELETECHECKIN','UNASSIGNED') ),
   UNIQUE(roleid, permission)
);