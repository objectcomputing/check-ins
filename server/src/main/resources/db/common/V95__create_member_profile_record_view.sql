DROP VIEW IF EXISTS member_profile_record;
CREATE VIEW member_profile_record AS
    SELECT mp.id, mp.firstname, mp.lastname, mp.title, mp.location, mp.workemail, mp.startdate,
           '1 day' AS tenure,
           CONCAT(pdl.firstname, ' ', pdl.lastname) AS pdlname,
           pdl.workemail AS pdlemail,
           CONCAT(supervisor.firstname, ' ', supervisor.lastname) AS supervisorname,
           supervisor.workemail AS supervisoremail
    FROM member_profile mp
    JOIN member_profile pdl ON mp.pdlid = pdl.id
    JOIN member_profile supervisor ON mp.supervisorid = supervisor.id;