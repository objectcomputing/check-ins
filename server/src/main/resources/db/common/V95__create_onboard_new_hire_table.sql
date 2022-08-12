CREATE TABLE IF NOT EXISTS new_hire_account (
    new_hire_account_id varchar PRIMARY KEY,
    email_address varchar(256),
    created_instant timestamp,
    changed_instant timestamp,
    state varchar(128)
);
