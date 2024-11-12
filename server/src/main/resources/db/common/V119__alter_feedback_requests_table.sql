-- Migration to add 'denied' column to the feedback_requests table

BEGIN;

ALTER TABLE feedback_requests
ADD COLUMN denied BOOLEAN DEFAULT FALSE NOT NULL;

COMMENT ON COLUMN feedback_requests.denied IS 'Indicates whether the feedback request has been denied.';

COMMIT;
