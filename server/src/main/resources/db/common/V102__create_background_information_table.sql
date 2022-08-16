 CREATE TABLE background_information(
    id varchar PRIMARY KEY,
    userid varchar UNIQUE references new_hire_account(email_address),
    stepcomplete BOOLEAN
);