drop table if exists role_permissions;
CREATE TABLE role_permissions(
    roleid varchar references role(id),
    permissionid varchar references permissions(id),
    primary key(roleid, permissionid)
);