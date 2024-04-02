CREATE TABLE tmp_role_permissions
(
    roleid     varchar references role (id),
    permission varchar,
    primary key (roleid, permission)
);



INSERT INTO tmp_role_permissions (roleid, permission)
SELECT rp.roleid, p.permission
FROM role_permissions rp
         JOIN
     permissions p
     ON
         rp.permissionid = p.id;


DROP TABLE role_permissions;
DROP TABLE permissions;

ALTER TABLE tmp_role_permissions
    RENAME TO role_permissions;
