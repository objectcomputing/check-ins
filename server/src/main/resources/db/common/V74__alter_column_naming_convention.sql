-- Rename columns in table feedback_requests

ALTER TABLE feedback_requests
RENAME COLUMN creatorId TO creator_id;

ALTER TABLE feedback_requests
RENAME COLUMN requesteeId TO requestee_id;

ALTER TABLE feedback_requests
RENAME COLUMN recipientId TO recipient_id;

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
DROP COLUMN active;

ALTER TABLE feedback_templates
RENAME COLUMN createdBy TO creator_id;

ALTER TABLE feedback_templates
ADD COLUMN date_created date;

ALTER TABLE feedback_templates
ADD COLUMN updater_id varchar REFERENCES member_profile(id);

ALTER TABLE feedback_templates
ADD COLUMN date_updated date;


-- Rename columns in table template_questions

ALTER TABLE template_questions
RENAME COLUMN templateId TO template_id;

ALTER TABLE template_questions
RENAME COLUMN questionNumber TO question_number;