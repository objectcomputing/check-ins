ALTER TABLE feedback_templates
DROP COLUMN active;

ALTER TABLE feedback_templates
RENAME COLUMN createdBy TO creatorId;

ALTER TABLE feedback_templates
ADD COLUMN dateCreated date;

ALTER TABLE feedback_templates
ADD COLUMN updaterId varchar REFERENCES member_profile(id);

ALTER TABLE feedback_templates
ADD COLUMN dateUpdated date;