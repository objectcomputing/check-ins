CREATE TABLE working_environment(
        working_environment_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        new_hire_account_id UUID NOT NULL,
        work_location varchar,
        key_type varchar,
        os_type varchar,
        accessories varchar,
        other_accessories varchar,
        CONSTRAINT fk_new_hire
                FOREIGN KEY(new_hire_account_id)
                REFERENCES new_hire_account(new_hire_account_id)
                ON DELETE CASCADE
);