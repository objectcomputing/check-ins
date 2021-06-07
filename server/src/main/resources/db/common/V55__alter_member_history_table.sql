ALTER TABLE member_history
ADD COLUMN id varchar PRIMARY KEY;

ALTER TABLE member_history
RENAME COLUMN dateTime TO date;

