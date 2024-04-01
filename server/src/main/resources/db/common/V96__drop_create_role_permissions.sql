drop table if exists role_permissions;
CREATE TABLE role_permissions(
                                 roleid varchar references role(id),
                                 permission varchar,
                                 primary key(roleid, permission)
);
drop table if exists permissions;