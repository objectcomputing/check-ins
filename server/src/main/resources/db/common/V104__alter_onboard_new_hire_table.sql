CREATE TABLE IF NOT EXISTS new_hire_account (
     new_hire_account_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     email_address varchar(256),
     created_instant timestamp,
     changed_instant timestamp,
     state varchar(128)
);