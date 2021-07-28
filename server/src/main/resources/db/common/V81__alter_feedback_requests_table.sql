ALTER TABLE feedback_requests
ADD COLUMN template_id varchar REFERENCES feedback_templates(id);