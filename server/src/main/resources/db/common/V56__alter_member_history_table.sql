ALTER TABLE member_history
<<<<<<< HEAD
DROP CONSTRAINT member_history_teamid_fkey;

ALTER TABLE member_history
ADD COLUMN teamName varchar;
=======
ADD COLUMN id varchar PRIMARY KEY;

ALTER TABLE member_history
RENAME COLUMN dateTime TO date;
>>>>>>> 22b24b3b (Fixed SQL numbering)
