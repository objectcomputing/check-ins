DROP TABLE IF EXISTS feedback_requests;

CREATE TABLE feedback_requests (
  id varchar PRIMARY KEY,
  creatorId varchar REFERENCES member_profile(id),
  requesteeId varchar REFERENCES member_profile(id),
  recipientId varchar REFERENCES member_profile(id),
  templateId varchar,
  sendDate date,
  dueDate date,
  submitDate date,
  status varchar
);
