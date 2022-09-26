CREATE TABLE onboardee_about(
        about_you_id varchar PRIMARY KEY,
        new_hire_account_id UUID NOT NULL,
        tshirt_size varchar,
        google_training varchar,
        introduction varchar,
        vaccine_status BOOLEAN,
        vaccine_two_weeks BOOLEAN,
        other_training varchar,
        additional_skills varchar,
        certifications varchar,
        CONSTRAINT fk_new_hire
                FOREIGN KEY(new_hire_account_id)
                REFERENCES new_hire_account(new_hire_account_id)
                ON DELETE CASCADE
);
