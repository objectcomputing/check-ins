DROP TABLE IF EXISTS role_documentation;
DROP TABLE IF EXISTS document;

CREATE TABLE document
(
    document_id varchar PRIMARY KEY,
    name        varchar NOT NULL UNIQUE,
    url         varchar NOT NULL UNIQUE,
    description varchar
);

CREATE TABLE role_documentation
(
    role_id       varchar REFERENCES role (id),
    document_id   varchar REFERENCES document (document_id),
    display_order integer NOT NULL,
    PRIMARY KEY (role_id, document_id)
);
