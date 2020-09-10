drop table if exists repeatable_table;
CREATE TABLE repeatable_table(
   id varchar PRIMARY KEY,
   dependOnVersionedField varchar REFERENCES member_profile(id),
   fakeField varchar
);