 CREATE TABLE background_information(
    id varchar PRIMARY KEY,
    userid varchar UNIQUE references new_hire_account(new_hire_account_id),
    stepcomplete BOOLEAN
);