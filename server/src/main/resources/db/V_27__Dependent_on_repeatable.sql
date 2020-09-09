drop table if exists versioned_table_dependent_on_repeatable_table;
CREATE TABLE versioned_table_dependent_on_repeatable_table(
   id varchar PRIMARY KEY,
   repeatableId varchar REFERENCES repeatable_table(id),
   description varchar
);