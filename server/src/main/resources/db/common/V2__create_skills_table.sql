drop table if exists skills;
CREATE TABLE skills(
   skillid varchar PRIMARY KEY,
   name varchar UNIQUE,
   pending boolean
);
