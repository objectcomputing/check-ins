drop table if exists gcp_entries;
CREATE TABLE gcp_entries(
   id varchar PRIMARY KEY,
   space_id varchar,
   member_id varchar REFERENCES member_profile(id)
);