ALTER TABLE role_permissions
ALTER COLUMN permission TYPE VARCHAR;

ALTER TABLE role_permissions
ADD CONSTRAINT available_permissions CHECK (permission IN ('ADMIN', 'PDL', 'MEMBER') );