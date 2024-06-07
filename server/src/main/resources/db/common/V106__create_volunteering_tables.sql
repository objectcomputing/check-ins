DROP TABLE IF EXISTS volunteering_relationship;
DROP TABLE IF EXISTS volunteering_event;
DROP TABLE IF EXISTS volunteering_organization;

CREATE TABLE volunteering_organization
(
    organization_id varchar PRIMARY KEY,
    description     varchar,
    website         varchar,
    is_active       boolean NOT NULL DEFAULT TRUE
);

CREATE TABLE volunteering_relationship
(
    relationship_id varchar PRIMARY KEY,
    member_id       varchar REFERENCES member_profile (id),
    organization_id varchar REFERENCES volunteering_organization (organization_id),
    start_date      timestamp NOT NULL,
    end_date        timestamp
);

CREATE TABLE volunteering_event
(
    event_id        varchar PRIMARY KEY,
    relationship_id varchar REFERENCES volunteering_relationship (relationship_id),
    event_date      timestamp NOT NULL,
    hours           integer   NOT NULL DEFAULT 0,
    notes           varchar
);
