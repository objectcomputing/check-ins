ALTER TABLE skills
ADD COLUMN description varchar;

ALTER TABLE skills
ADD COLUMN extraneous boolean NOT NULL DEFAULT FALSE;

