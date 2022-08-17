 CREATE TABLE IF NOT EXISTS background_information(
            background_information_id varchar PRIMARY KEY,
            new_hire_account_id UUID NOT NULL,
            step_complete BOOLEAN
            CONSTRAINT fk_new_hire
                    FOREIGN KEY(new_hire_account_id)
                    REFERENCES new_hire_account(new_hire_account_id)
                    ON DELETE CASCADE
);