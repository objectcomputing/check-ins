DROP FUNCTION IF EXISTS split_name(varchar);
DROP TYPE IF EXISTS FullNamePair;

CREATE TYPE FullNamePair AS (firstName varchar, lastName varchar);

CREATE OR REPLACE FUNCTION split_name(name varchar) RETURNS FullNamePair
AS $$
DECLARE
    result FullNamePair;
BEGIN
    result.firstName := SUBSTRING(name, 1, POSITION(' ' in name) - 1);
    result.lastName := SUBSTRING(name, POSITION(' ' in name) + 1, CHAR_LENGTH(name) - POSITION(' ' in name));
    RETURN result;
END;
$$ LANGUAGE plpgsql;

ALTER TABLE member_profile
    ADD COLUMN firstName varchar,
    ADD COLUMN middleName varchar,
    ADD COLUMN lastName varchar,
    ADD COLUMN suffix varchar;

DO $$
DECLARE
    row_var RECORD;
    name_var FullNamePair;
BEGIN
    FOR row_var IN SELECT id, PGP_SYM_DECRYPT(name::bytea, '${aeskey}') AS name FROM member_profile LOOP
		SELECT * INTO name_var FROM split_name(row_var.name);
        UPDATE member_profile SET firstName = PGP_SYM_ENCRYPT(name_var.firstName, '${aeskey}'),
        lastName = PGP_SYM_ENCRYPT(name_var.lastName, '${aeskey}') WHERE id = row_var.id;
    END LOOP;
END;
$$;

ALTER TABLE member_profile
    ALTER COLUMN firstName SET NOT NULL,
    ALTER COLUMN lastName SET NOT NULL;

ALTER TABLE member_profile DROP COLUMN name;
