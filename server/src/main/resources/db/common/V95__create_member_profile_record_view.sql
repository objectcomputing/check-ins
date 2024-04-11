DROP VIEW IF EXISTS member_profile_record;
CREATE VIEW member_profile_record AS
    SELECT mp.id, mp.firstname, mp.lastname, mp.title, mp.location, mp.workemail, mp.startdate,
           pdl.firstname AS pdlfirstname, pdl.lastname AS pdllastname,
           pdl.workemail AS pdlemail,
           supervisor.firstname AS supervisorfirstname, supervisor.lastname as supervisorlastname,
           supervisor.workemail AS supervisoremail
    FROM member_profile mp
    LEFT JOIN member_profile pdl ON mp.pdlid = pdl.id
    LEFT JOIN member_profile supervisor ON mp.supervisorid = supervisor.id;