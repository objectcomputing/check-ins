-- Drop in this order to avoid foreign key constraint errors
DROP TABLE IF EXISTS earned_certification;
DROP TABLE IF EXISTS certification;

CREATE TABLE certification
(
    certification_id varchar PRIMARY KEY,
    name             varchar NOT NULL,
    badge_url        varchar,
    is_active        boolean NOT NULL DEFAULT TRUE
);

CREATE TABLE earned_certification
(
    earned_certification_id varchar PRIMARY KEY,
    member_id               varchar REFERENCES member_profile (id),
    certification_id        varchar REFERENCES certification (certification_id),
    description             varchar   NOT NULL,
    earned_date             timestamp NOT NULL,
    expiration_date         timestamp,
    certificate_image_url   varchar
);
