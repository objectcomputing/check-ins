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

DROP TABLE IF EXISTS automated_kudos_read_time;

CREATE TABLE automated_kudos_read_time
(
    id              varchar PRIMARY KEY,
    readtime        timestamp
);
