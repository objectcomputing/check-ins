DROP TABLE IF EXISTS member_roles;

CREATE TABLE member_roles(
	roleid varchar REFERENCES role(id),
	memberid varchar REFERENCES member_profile(id),
    PRIMARY KEY(roleid, memberid)
);
