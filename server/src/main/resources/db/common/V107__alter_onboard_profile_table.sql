CREATE TABLE IF NOT EXISTS onboard_profile(
        onboard_profile_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
        new_hire_account_id UUID NOT NULL,
        firstname varchar,
        middlename varchar,
        lastname varchar,
        socialsecuritynumber varchar,
        birthdate varchar,
        currentaddress varchar,
        previousaddress varchar,
        phonenumber varchar,
        secondphonenumber varchar,
        personalemail varchar,
        CONSTRAINT fk_new_hire
                FOREIGN KEY(new_hire_account_id)
                REFERENCES new_hire_account(new_hire_account_id)
                ON DELETE CASCADE
);