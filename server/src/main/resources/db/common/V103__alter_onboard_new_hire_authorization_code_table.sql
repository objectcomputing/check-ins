CREATE TABLE IF NOT EXISTS new_hire_authorization_code (
    new_hire_authorization_code_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    new_hire_account_id UUID NOT NULL,
    salt varchar(256),
    verifier varchar(256),
    purpose varchar(128),
    source varchar(128),
    issued_instant timestamp,
    time_to_live bigint,
    consumed_instant timestamp,
    CONSTRAINT fk_new_hire
        FOREIGN KEY(new_hire_account_id)
        REFERENCES new_hire_account(new_hire_account_id)
        ON DELETE CASCADE
);