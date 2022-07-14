ALTER TABLE checkins
ADD FOREIGN KEY (pdlId) REFERENCES member_profile(uuid);