ALTER TABLE new_hire_credentials

ALTER COLUMN new_hire_credentials_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
ALTER COLUMN new_hire_account_id UUID NOT NULL;