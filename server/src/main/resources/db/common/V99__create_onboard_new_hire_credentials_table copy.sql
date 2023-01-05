CREATE TABLE IF NOT EXISTS new_hire_credentials (
    new_hire_credentials_id varchar PRIMARY KEY,
    new_hire_account_id varchar,
    salt varchar(256),
    verifier VARCHAR(256),
    CONSTRAINT fk_new_hire
        FOREIGN KEY(new_hire_account_id)
        REFERENCES new_hire_account(new_hire_account_id)
        ON DELETE CASCADE
);
