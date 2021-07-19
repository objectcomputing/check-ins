-- Rename columns in table feedback_requests

ALTER TABLE feedback_requests
RENAME COLUMN creatorId TO creator_id;

ALTER TABLE feedback_requests
RENAME COLUMN requesteeId TO requestee_id;

ALTER TABLE feedback_requests
RENAME COLUMN recipientId TO recipient_id;

ALTER TABLE feedback_requests
RENAME COLUMN templateId TO template_id;

ALTER TABLE feedback_requests
RENAME COLUMN sendDate TO send_date;

ALTER TABLE feedback_requests
RENAME COLUMN dueDate TO due_date;

ALTER TABLE feedback_requests
RENAME COLUMN submitDate TO submit_date;


-- Rename columns in table feedback_request_questions

ALTER TABLE feedback_request_questions
RENAME COLUMN requestId to request_id;

ALTER TABLE feedback_request_questions
RENAME COLUMN questionContent TO question;

ALTER TABLE feedback_request_questions
RENAME COLUMN orderNum TO question_number;


-- Rename columns in table feedback_templates

ALTER TABLE feedback_templates
RENAME COLUMN creatorId TO creator_id;

ALTER TABLE feedback_templates
RENAME COLUMN dateCreated TO date_created;

ALTER TABLE feedback_templates
RENAME COLUMN updaterId TO updater_id;

ALTER TABLE feedback_templates
RENAME COLUMN dateUpdated TO date_updated;


-- Rename columns in table template_questions

ALTER TABLE template_questions
RENAME COLUMN templateId TO template_id;

ALTER TABLE template_questions
RENAME COLUMN questionNumber TO question_number;