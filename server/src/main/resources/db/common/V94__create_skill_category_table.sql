DROP TABLE if exists skillcategories;
CREATE TABLE skillcategories (
   id varchar PRIMARY KEY,
   name varchar UNIQUE ,
   description varchar
);
