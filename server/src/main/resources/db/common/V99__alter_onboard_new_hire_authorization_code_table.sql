ALTER TABLE new_hire_authorization_code

ALTER COLUMN COLUMN new_hire_authorization_code_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
ALTER COLUMN COLUMN new_hire_account_id UUID NOT NULL;