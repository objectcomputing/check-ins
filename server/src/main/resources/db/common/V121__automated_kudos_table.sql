DROP TABLE IF EXISTS automated_kudos;

CREATE TABLE automated_kudos
(
    id              varchar PRIMARY KEY,
    requested       boolean,
    message         varchar,
    externalid      varchar,
    senderid        varchar REFERENCES member_profile (id),
    recipientids    varchar[]
);
