DROP TABLE IF EXISTS demographics;

CREATE TABLE demographics (
    id varchar PRIMARY KEY,
    memberId varchar REFERENCES member_profile(id),
    gender varchar,
    degreeLevel varchar,
    industryTenure Integer,
    personOfColor BOOLEAN,
    veteran BOOLEAN,
    militaryTenure Integer,
    militaryBranch varchar
);