ALTER TABLE feedback_requests
ADD COLUMN recipientId varchar REFERENCES member_profile(id);

ALTER TABLE feedback_requests
ADD COLUMN submitDate date;

ALTER TABLE feedback_requests
ADD COLUMN sentiment decimal;
