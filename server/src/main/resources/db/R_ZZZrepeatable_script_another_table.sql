drop table if exists repeatable_table_electric_boogaloo;
CREATE TABLE repeatable_table_electric_boogaloo(
   id varchar PRIMARY KEY,
   repeatableId varchar REFERENCES repeatable_table(id),
   dependOnVersionedField varchar REFERENCES versioned_table_dependent_on_repeatable_table(id),
   fakeField2 varchar
);