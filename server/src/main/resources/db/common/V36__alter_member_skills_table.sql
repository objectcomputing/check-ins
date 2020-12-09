ALTER TABLE role
DROP CONSTRAINT role_memberid_fkey;

ALTER TABLE role
ADD CONSTRAINT role_memberid_fkey FOREIGN KEY (memberid) REFERENCES member_profile(id) ON DELETE CASCADE;