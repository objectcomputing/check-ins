CREATE TABLE onboardee_employment_eligibility(
            employment_eligibility_id varchar PRIMARY KEY,
            new_hire_account_id UUID NOT NULL,
            age_legal BOOLEAN,
            us_citizen BOOLEAN,
            visa_status varchar,
            expiration_date varchar,
            felony_status BOOLEAN,
            felony_explanation varchar,
            CONSTRAINT fk_new_hire
                    FOREIGN KEY(new_hire_account_id)
                    REFERENCES new_hire_account(new_hire_account_id)
                    ON DELETE CASCADE
);
