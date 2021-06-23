DROP TABLE IF EXISTS employee_hours;

CREATE TABLE employee_hours (
    id varchar PRIMARY KEY,
    employeeId varchar REFERENCES member_profile(employeeId),
    contributionHours decimal,
    billableHours decimal,
    ptoHours decimal
);
