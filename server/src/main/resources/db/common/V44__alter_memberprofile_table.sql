ALTER TABLE member_profile
ADD firstName varchar NOT NULL, middleName varchar, lastName varchar NOT NULL, suffix varchar;

CREATE OR REPLACE PROCEDURE split_name(IN name varchar, OUT firstName varchar, OUT lastName varchar)
LANGUAGE plpgsql
AS $$
BEGIN
    firstName := SUBSTRING(name, 1, POSITION(' ', name) - 1)
    lastName := SUBSTRING(name, POSITION(' ', name) + 1, LEN(name) - POSITION(' ', name))
END;
$$;

DO $$
DECLARE
    row RECORD;
    first_name varchar;
    last_name varchar;
BEGIN
    FOR row IN SELECT id, name FROM member_profile LOOP
        EXECUTE 'CALL split_name($1, first_name, last_name);' USING row.name;
        EXECUTE 'UPDATE member_profile SET firstName = $1, lastName = $2 WHERE id = $3;'
            USING first_name, last_name, row.id;
    END LOOP;
END;
$$;

DROP COLUMN name;
