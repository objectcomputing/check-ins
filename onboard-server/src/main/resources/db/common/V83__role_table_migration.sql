DROP TABLE role_permissions;

CREATE TABLE role_copy AS TABLE role;

ALTER TABLE role
DROP COLUMN memberid;

-- remove duplicate entries so only single ADMIN, MEMBER, and PDL role exists
DELETE FROM role a
WHERE a.ctid <> (SELECT min(b.ctid)
                 FROM   role b
                 WHERE  a.role = b.role);


ALTER TABLE role_copy
ADD COLUMN true_role_id VARCHAR;

-- associate role id from role entry to all matching role names in role_copy.role
UPDATE
    role_copy
SET
    true_role_id = role.id
FROM
    role
where
    role_copy.role = role.role;



DROP TABLE IF EXISTS member_roles;
CREATE TABLE member_roles(
	roleid varchar REFERENCES role(id),
	memberid varchar REFERENCES member_profile(id),
    PRIMARY KEY(roleid, memberid)
);

-- use role_copy to make association and migrate data
INSERT INTO member_roles (memberid, roleid)
SELECT member_profile.id, role.id
FROM
    role_copy
JOIN
    member_profile
ON
    member_profile.id = role_copy.memberid
JOIN
    role
ON
    role.id = role_copy.true_role_id;

drop table role_copy;


ALTER TABLE role ALTER COLUMN role TYPE varchar;

create unique index case_insensitive_role_name on role (lower(role));

ALTER TABLE role DROP CONSTRAINT available_roles;

