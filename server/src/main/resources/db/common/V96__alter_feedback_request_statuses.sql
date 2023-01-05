UPDATE feedback_requests
SET status = 'SUBMITTED'
WHERE status = 'submitted';

UPDATE feedback_requests
SET status = 'SENT'
WHERE status = 'pending' OR status = 'sent';

UPDATE feedback_requests
SET status = 'CANCELED'
WHERE status = 'canceled';