ALTER TABLE member_profile
ADD FOREIGN KEY (pdlId) REFERENCES member_profile(uuid); 