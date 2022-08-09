CREATE TABLE new_hire_account(
    new_hire_account_id varchar PRIMARY KEY,
    state varchar,
    email_address varchar(255),
    created_instant timestamp,
    changed_instant timestamp
);
