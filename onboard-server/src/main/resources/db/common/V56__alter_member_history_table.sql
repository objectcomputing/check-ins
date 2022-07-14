ALTER TABLE member_history

DROP CONSTRAINT member_history_teamid_fkey;

ALTER TABLE member_history
ADD COLUMN teamName varchar;

