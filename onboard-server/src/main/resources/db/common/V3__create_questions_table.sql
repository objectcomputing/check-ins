drop table if exists questions;
CREATE TABLE questions(
   questionid varchar PRIMARY KEY,
   text varchar UNIQUE
);