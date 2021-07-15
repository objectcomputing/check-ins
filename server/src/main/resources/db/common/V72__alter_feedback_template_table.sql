ALTER TABLE feedback_templates
DROP COLUMN active,
ADD COLUMN updatedOn date;