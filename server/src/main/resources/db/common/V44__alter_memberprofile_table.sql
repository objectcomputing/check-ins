CREATE TYPE FullName AS (firstName varchar, lastName varchar);

CREATE OR REPLACE FUNCTION split_name(name varchar) RETURNS FullName
AS $$
DECLARE
    result FullName;
BEGIN
    result.firstName := SUBSTRING(name, 1, POSITION(' ' in name) - 1);
    result.lastName := SUBSTRING(name, POSITION(' ' in name) + 1, LEN(name) - POSITION(' ' in name));
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
    row RECORD;
    fullName FullName;
BEGIN
    FOR row IN SELECT id, name FROM member_profile LOOP
        EXECUTE 'SELECT split_name($1) AS fullName;' USING row.name;
        EXECUTE 'UPDATE member_profile SET firstName = $1, lastName = $2 WHERE id = $3;'
            USING fullName.firstName, fullName.lastName, row.id;
        --SELECT split_name(row.name) AS fullName;
        --UPDATE member_profile SET firstName = fullName.firstName, lastName = fullName.lastName WHERE id = row.id;
    END LOOP;
END;
$$;

ALTER TABLE member_profile DROP COLUMN name;
ALTER TABLE member_profile
    ALTER COLUMN firstName SET NOT NULL,
    ALTER COLUMN lastName SET NOT NULL;
