ALTER TABLE feedback_requests
ADD COLUMN reason VARCHAR(255);

COMMENT ON COLUMN feedback_requests.reason IS 'Reason provided when the feedback request is denied.';