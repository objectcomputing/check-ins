ALTER TABLE new_hire_account
ALTER COLUMN COLUMN new_hire_account_id UUID PRIMARY KEY DEFAULT uuid_generate_v4();