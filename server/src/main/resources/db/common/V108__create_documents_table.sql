DROP TABLE IF EXISTS role_documents;
DROP TABLE IF EXISTS documents;

CREATE TABLE documents(
  id varchar PRIMARY KEY,
  name varchar,
  description varchar,
  url varchar
);

CREATE TABLE role_documents(
    roleid varchar REFERENCES role(id),
    documentid varchar REFERENCES documents(id),
    documentnumber smallint,
    PRIMARY KEY(roleid, documentid)
);
