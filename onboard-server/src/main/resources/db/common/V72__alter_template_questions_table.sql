ALTER TABLE template_questions
RENAME COLUMN orderNum TO questionNumber;

ALTER TABLE template_questions
ADD CONSTRAINT template_questions_uniqueconstraint UNIQUE (templateId, questionNumber);