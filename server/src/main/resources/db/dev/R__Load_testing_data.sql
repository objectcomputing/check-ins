delete from action_items;
delete from agenda_items;
delete from checkin_document;
delete from checkin_notes;
delete from private_notes;
delete from checkins;
delete from guild_member_history;
delete from guild_member;
delete from guild;
delete from member_skills;
delete from pulse_response;
delete from questions;
delete from member_roles;
delete from role_permissions;
delete from permissions;
delete from role;
delete from kudos_recipient;
delete from kudos;
delete from team_member;
delete from team;
delete from feedback_answers;
delete from feedback_requests;
delete from template_questions;
delete from review_periods;
delete from feedback_templates;
delete from emails;
delete from member_history;
delete from member_profile;
delete from skillcategory_skills;
delete from skills;
delete from skillcategories;
delete from earned_certification;
delete from certification;

-- Member Profiles
INSERT INTO member_profile -- Gina Bremehr
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('01b7d769-9fa2-43ff-95c7-f3b950a27bf9', PGP_SYM_ENCRYPT('Gina','${aeskey}'), PGP_SYM_ENCRYPT('Bremehr','${aeskey}'), PGP_SYM_ENCRYPT('COO','${aeskey}'), PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bremehrg@objectcomputing.com','${aeskey}'), '12312345', '2012-09-20', PGP_SYM_ENCRYPT('Epitome of Strong Woman','${aeskey}'), null, '1988-09-21', '2012-09-20');

INSERT INTO member_profile -- Big Boss
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', PGP_SYM_ENCRYPT('Big','${aeskey}'),  PGP_SYM_ENCRYPT('Boss','${aeskey}'), PGP_SYM_ENCRYPT('Sneaky Snake','${aeskey}'), PGP_SYM_ENCRYPT('Mother Base','${aeskey}'), PGP_SYM_ENCRYPT('bossb@objectcomputing.com','${aeskey}'), '351242153', '2022-03-29', PGP_SYM_ENCRYPT('The Legendary Big Boss','${aeskey}'), null, '1943-07-04', '2022-03-29');

INSERT INTO member_profile -- Michael Kimberlin
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('6207b3fd-042d-49aa-9e28-dcc04f537c2d', PGP_SYM_ENCRYPT('Michael','${aeskey}'), PGP_SYM_ENCRYPT('Kimberlin','${aeskey}'), PGP_SYM_ENCRYPT('Director of Organizational Development','${aeskey}'), PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('kimberlinm@objectcomputing.com','${aeskey}'), '12312342', '2012-09-29', PGP_SYM_ENCRYPT('Developer of developers and others','${aeskey}'), '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2012-09-29');

INSERT INTO member_profile -- Ron Steinkamp
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('Ron','${aeskey}'), PGP_SYM_ENCRYPT('Steinkamp','${aeskey}'), PGP_SYM_ENCRYPT('Senior Project Manager','${aeskey}'), '01b7d769-9fa2-43ff-95c7-f3b950a27bf9',  PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('steinkampr@objectcomputing.com','${aeskey}'), '12312346', '2012-09-29', PGP_SYM_ENCRYPT('Managing projects well','${aeskey}'), null, '1988-09-02', '2012-09-29');

INSERT INTO member_profile -- John Meyerin
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('John','${aeskey}'), PGP_SYM_ENCRYPT('Meyerin','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('meyerinj@objectcomputing.com','${aeskey}'), '12312347', '2012-09-29', PGP_SYM_ENCRYPT('Outstanding Engineer','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2012-09-29');

INSERT INTO member_profile -- Geetika Sharma
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika','${aeskey}'), PGP_SYM_ENCRYPT('Sharma','${aeskey}'), PGP_SYM_ENCRYPT('PMO Administrator','${aeskey}'), '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('sharmag@objectcomputing.com','${aeskey}'), '12312348', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Wrangler Extrodinaire','${aeskey}'), null, '2012-09-29');

INSERT INTO member_profile -- Holly Williams
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Holly','${aeskey}'), PGP_SYM_ENCRYPT('Williams','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('williamsh@objectcomputing.com','${aeskey}'), '12312349', '2012-09-29', PGP_SYM_ENCRYPT('Software Engineer Remarkable','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2012-09-29');

INSERT INTO member_profile -- Mark Volkmann
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', PGP_SYM_ENCRYPT('Mark','${aeskey}'), PGP_SYM_ENCRYPT('Volkmann','${aeskey}'), PGP_SYM_ENCRYPT('Partner and Distinguished Engineer','${aeskey}'), PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('volkmannm@objectcomputing.com','${aeskey}'), '12312343', '2012-09-29', PGP_SYM_ENCRYPT('Software Engineer Spectacular','${aeskey}'), null, '2012-09-29');

INSERT INTO member_profile -- Pramukh Bagur
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('Pramukh','${aeskey}'), PGP_SYM_ENCRYPT('Bagur','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bagurp@objectcomputing.com','${aeskey}'), '12312344', '2012-09-29', PGP_SYM_ENCRYPT('Top notch Engineer','${aeskey}'), '72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', '2012-09-29');

INSERT INTO member_profile -- Jesse Hanner
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesse','${aeskey}'), PGP_SYM_ENCRYPT('Hanner','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('hannerj@objectcomputing.com','${aeskey}'), '123123450', '2012-09-29', PGP_SYM_ENCRYPT('Amazing Engineer','${aeskey}'), null, '2012-09-29');

INSERT INTO member_profile -- Suman Maroju
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('1b4f99da-ef70-4a76-9b37-8bb783b749ad', PGP_SYM_ENCRYPT('Suman','${aeskey}'), PGP_SYM_ENCRYPT('Maroju','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('marojus@objectcomputing.com','${aeskey}'), '123123410', '2012-09-29', PGP_SYM_ENCRYPT('Superior Engineer','${aeskey}'), '72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', '2012-09-29');

INSERT INTO member_profile -- Mohit Bhatia
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('b2d35288-7f1e-4549-aa2b-68396b162490', PGP_SYM_ENCRYPT('Mohit','${aeskey}'), PGP_SYM_ENCRYPT('Bhatia','${aeskey}'), PGP_SYM_ENCRYPT('Principal Software Engineer','${aeskey}'), '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bhatiam@objectcomputing.com','${aeskey}'), '123123411', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Extraordinaire','${aeskey}'), '72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', '2012-09-29');

INSERT INTO member_profile -- Zack Brown
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('43ee8e79-b33d-44cd-b23c-e183894ebfef', PGP_SYM_ENCRYPT('Zack','${aeskey}'), PGP_SYM_ENCRYPT('Brown','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('brownz@objectcomputing.com','${aeskey}'), '123123412', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Phenomenal','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2012-09-29');

INSERT INTO member_profile -- Joe Warner
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, last_seen)
VALUES
('066b186f-1425-45de-89f2-4ddcc6ebe237', PGP_SYM_ENCRYPT('Joe','${aeskey}'), PGP_SYM_ENCRYPT('Warner','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('warnerj@objectcomputing.com','${aeskey}'), '123123413', '2012-09-29', PGP_SYM_ENCRYPT('Engineer of Supreme Ability','${aeskey}'), null, '2012-09-29');

INSERT INTO member_profile -- Julia Smith
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia','${aeskey}'),  PGP_SYM_ENCRYPT('Smith','${aeskey}'), PGP_SYM_ENCRYPT('Intern','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('smithj@objectcomputing.com','${aeskey}'), '010101010', '2021-05-22', PGP_SYM_ENCRYPT('Local creature in discovery room','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1998-07-07', '2021-05-22');

INSERT INTO member_profile -- Faux Freddy
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('2dee821c-de32-4d9c-9ecb-f73e5903d17a', PGP_SYM_ENCRYPT('Faux','${aeskey}'),  PGP_SYM_ENCRYPT('Freddy','${aeskey}'), PGP_SYM_ENCRYPT('Test Engineer','${aeskey}'), '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('testing@objectcomputing.com','${aeskey}'), '010101011', '2021-05-22', PGP_SYM_ENCRYPT('Test user 1','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1950-01-01', '2021-05-22');

INSERT INTO member_profile -- Unreal Ulysses
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('dfe2f986-fac0-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Unreal','${aeskey}'),  PGP_SYM_ENCRYPT('Ulysses','${aeskey}'), PGP_SYM_ENCRYPT('Test Engineer 2','${aeskey}'), '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('testing2@objectcomputing.com','${aeskey}'), '010101012', '2021-05-22', PGP_SYM_ENCRYPT('Test user 2','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1950-01-01', '2021-05-22');

INSERT INTO member_profile -- Kazuhira Miller
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('a90be358-aa3d-49c8-945a-879a93646e45', PGP_SYM_ENCRYPT('Kazuhira','${aeskey}'),  PGP_SYM_ENCRYPT('Miller','${aeskey}'), PGP_SYM_ENCRYPT('Unit Coordinator','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', PGP_SYM_ENCRYPT('Mother Base','${aeskey}'), PGP_SYM_ENCRYPT('millerkaz@objectcomputing.com','${aeskey}'), '012345678', '2022-03-29', PGP_SYM_ENCRYPT('Bff with Big Boss','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', '1943-07-04', '2022-03-29');

INSERT INTO member_profile -- Revolver Ocelot
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('105f2968-a182-45a3-892c-eeff76383fe0', PGP_SYM_ENCRYPT('Revolver','${aeskey}'),  PGP_SYM_ENCRYPT('Ocelot','${aeskey}'), PGP_SYM_ENCRYPT('Head of Sales, HR, Management','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', PGP_SYM_ENCRYPT('Mother Base','${aeskey}'), PGP_SYM_ENCRYPT('ocelotr@objectcomputing.com','${aeskey}'), '489102361', '2022-03-29', PGP_SYM_ENCRYPT('Loves to reload during battle','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', '1943-07-04', '2022-03-29');

INSERT INTO member_profile -- Huey Emmerich
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate, last_seen)
VALUES
('8d75c07e-6adc-437a-8659-7dd953ce6600', PGP_SYM_ENCRYPT('Huey','${aeskey}'),  PGP_SYM_ENCRYPT('Emmerich','${aeskey}'), PGP_SYM_ENCRYPT('Head of R&D','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', PGP_SYM_ENCRYPT('Mother Base','${aeskey}'), PGP_SYM_ENCRYPT('emmerichh@objectcomputing.com','${aeskey}'), '657483498', '2022-03-29', PGP_SYM_ENCRYPT('Waiting for love to bloom on the battlefield','${aeskey}'), '43ee8e79-b33d-44cd-b23c-e183894ebfef', '1943-07-04', '2022-03-29');


-- Roles
INSERT INTO role
    (id, description, role)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', PGP_SYM_ENCRYPT('is an admin','${aeskey}'), 'ADMIN');

INSERT INTO role
    (id, description, role)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', PGP_SYM_ENCRYPT('is a member','${aeskey}'), 'MEMBER');

INSERT INTO role
    (id, description, role)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', PGP_SYM_ENCRYPT('is a pdl','${aeskey}'), 'PDL');


-- Admin Role Assigning
INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '6207b3fd-042d-49aa-9e28-dcc04f537c2d'); -- Michael Kimberlin

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '2559a257-ae84-4076-9ed4-3820c427beeb'); -- Ron Steinkamp

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498'); -- Geetika Sharma

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '43ee8e79-b33d-44cd-b23c-e183894ebfef'); -- Zack Brown

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '6884ab96-2275-4af9-89d8-ad84254d8759'); -- Pramukh Bagur

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '8fa673c0-ca19-4271-b759-41cb9db2e83a'); -- Holly Williams

INSERT INTO member_roles(
    roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '066b186f-1425-45de-89f2-4ddcc6ebe237'); -- Joe Warner


-- PDL Role Assigning
INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '6207b3fd-042d-49aa-9e28-dcc04f537c2d'); -- Michael Kimberlin

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '2559a257-ae84-4076-9ed4-3820c427beeb'); -- Ron Steinkamp

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498'); -- Geetika Sharma

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d'); -- Mark Volkmann

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9'); -- Gina Bremehr

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '802cb1f5-a255-4236-8719-773fa53d79d9'); -- John Meyerin

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '6884ab96-2275-4af9-89d8-ad84254d8759'); -- Pramukh Bagur

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '8fa673c0-ca19-4271-b759-41cb9db2e83a'); -- Holly Williams

INSERT INTO member_roles(
    roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '066b186f-1425-45de-89f2-4ddcc6ebe237'); -- Joe Warner


-- Member Role Assigning
INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9'); -- Gina Bremehr

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '2559a257-ae84-4076-9ed4-3820c427beeb'); -- Ron Steinkamp

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '802cb1f5-a255-4236-8719-773fa53d79d9'); -- John Meyerin

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498'); -- Geetika Sharma

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '6207b3fd-042d-49aa-9e28-dcc04f537c2d'); -- Michael Kimberlin

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d'); -- Mark Volkmann

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3'); -- Jesse Hanner

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '6884ab96-2275-4af9-89d8-ad84254d8759'); -- Pramukh Bagur

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '1b4f99da-ef70-4a76-9b37-8bb783b749ad'); -- Suman Maroju

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'b2d35288-7f1e-4549-aa2b-68396b162490'); -- Mohit Bhatia

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '43ee8e79-b33d-44cd-b23c-e183894ebfef'); -- Zack Brown

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '8fa673c0-ca19-4271-b759-41cb9db2e83a'); -- Holly Williams


-- Teams
INSERT INTO team -- Checkins Experts
(id, name, description)
VALUES
('a8733740-cf4c-4c16-a8cf-4f928c409acc', PGP_SYM_ENCRYPT('Checkins Experts','${aeskey}'), PGP_SYM_ENCRYPT('Checkins Engineers of superior knowledge','${aeskey}'));

INSERT INTO team -- JavaScript Gurus
(id, name, description)
VALUES
('e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', PGP_SYM_ENCRYPT('JavaScript Gurus','${aeskey}'), PGP_SYM_ENCRYPT('JavaScript Engineers of Outstanding Skill','${aeskey}'));

INSERT INTO team -- Micronaut Genii
(id, name, description)
VALUES
('036b95a5-357c-45bd-b60e-e8e2e1afec83', PGP_SYM_ENCRYPT('Micronaut Genii','${aeskey}'), PGP_SYM_ENCRYPT('Micronaut Engineers of Genius Caliber','${aeskey}'));

INSERT INTO team -- PMO Superness
(id, name, description)
VALUES
('e545dfa1-a07d-4099-9a5b-ed14f07b87cc', PGP_SYM_ENCRYPT('PMO Superness','${aeskey}'), PGP_SYM_ENCRYPT('Excellent PMO Artists','${aeskey}'));


-- Team Members
---- Checkins Experts Members
INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('d2ee49cb-9479-49fb-80d7-43c3c1b50f91', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '802cb1f5-a255-4236-8719-773fa53d79d9', true); -- John Meyerin

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('c7b4d5e0-09ba-479a-8c40-ca9bbd8f217a', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '6884ab96-2275-4af9-89d8-ad84254d8759', false); -- Pramukh Bagur

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('20bf1ddb-53a0-436e-99dc-802c1199e282', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', false); -- Jesse Hanner

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('122ca588-bf61-4aea-bb6e-39838328bf85', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', 'b2d35288-7f1e-4549-aa2b-68396b162490', true); -- Mohit Bhatia

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('adff5631-d4dc-4c61-b3d4-232d1cce8ce0', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', false); -- Suman Maroju

---- JavaScript Gurus Members
INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('8eea2f65-160c-4db7-9f6d-f367acd333fb', 'e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', '43ee8e79-b33d-44cd-b23c-e183894ebfef', false); -- Zack Brown

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('f84a21ca-1579-4c6a-8148-6a355518797a', 'e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', true); -- Mark Volkmann

---- Micronaut Genii Members
INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('7cf7820a-b099-48e5-b630-4f921ee17d16', '036b95a5-357c-45bd-b60e-e8e2e1afec83', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false); -- Holly Williams

---- PMO Superness Members
INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('9e7e9577-a36b-4238-84cc-4f160ac60b40', 'e545dfa1-a07d-4099-9a5b-ed14f07b87cc', '2559a257-ae84-4076-9ed4-3820c427beeb', true); -- Ron Steinkamp

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('97f3a251-0ed2-449b-a756-07226e6e6522', 'e545dfa1-a07d-4099-9a5b-ed14f07b87cc', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', false); -- Geetika Sharma



-- Pramukh Bagur Check-ins
---- 2020-09-29 PM - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Michael Kimberlin
VALUES
('92e91c5a-cb00-461a-86b4-d01b3f07754e', '6884ab96-2275-4af9-89d8-ad84254d8759', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2020-09-29 17:40:29.04' , false);

INSERT INTO action_items
(id, checkinid, createdbyid, description) -- created by: Holly Williams
Values('b0840fc5-9a8e-43d8-be99-9682fc32e69e', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Action Item for Holly Williams','${aeskey}'));

INSERT INTO action_items
(id, checkinid, createdbyid, description) -- created by: Holly Williams
Values('9a779dec-c1b6-484e-ad76-38e7c06b011c', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Another Action Item for Holly Williams','${aeskey}'));

---- 2020-09-29 AM - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) --  pdl: John Meyerin
VALUES
('1343411e-26bf-4274-81ca-1b46ba3f0cb0', '6884ab96-2275-4af9-89d8-ad84254d8759', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-09-29 10:40:29.04' , false);

INSERT INTO action_items
(id, checkinid, createdbyid, description) -- created by: Pramukh Bagur
Values('0ead3434-82e7-47b4-a0ef-d1f44d01732b', '1343411e-26bf-4274-81ca-1b46ba3f0cb0', '6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('Action Item for Pramukh Bagur','${aeskey}'));


-- Function to generate a random time between 9 AM and 5 PM
CREATE OR REPLACE FUNCTION random_workday_time() RETURNS time AS $$
BEGIN
  RETURN '09:00:00'::time + (random() * (interval '8 hours'));
END;
$$ LANGUAGE plpgsql;

-- Mohit Bhatia Check-ins
---- 2020-09-29 - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Michael Kimberlin (reassigned to Geetika Sharma)
VALUES
('8aa38f8c-2169-41b1-8548-1c2472fab7ff', 'b2d35288-7f1e-4549-aa2b-68396b162490', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2020-09-29 15:40:29.04' , false);

---- NOW() - INTERVAL '2 weeks' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  'ce666f85-4289-4fcd-b3c2-365ab965e30a',
  'b2d35288-7f1e-4549-aa2b-68396b162490',
  '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', -- pdl: Geetika Sharma
  (CURRENT_DATE - INTERVAL '2 weeks') + random_workday_time(), -- 2 weeks ago with random time
  true
);

---- NOW() - INTERVAL '3 months' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  '13d76100-a6a4-4d87-82e3-9faac4ea1a09',
  'b2d35288-7f1e-4549-aa2b-68396b162490',
  '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', -- pdl: Geetika Sharma
  (CURRENT_DATE - INTERVAL '3 months') + random_workday_time(), -- 3 months ago with random time
  true
);


-- Zack Brown Check-ins
---- 2020-09-29 - Completed
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Mark Volkmann
VALUES
('bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '2020-09-29 11:32:29.04' , true);


-- Jesse Hanner Check-ins
---- 2020-03-20 - Completed
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Pramukh Bagur
VALUES
('ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '6884ab96-2275-4af9-89d8-ad84254d8759', '2020-03-20 11:32:29.04' , true);

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description) -- created by: Jesse Hanner
VALUES
('e5449026-cd9a-4bed-a648-fe3ad9382831', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesses note','${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description) -- created by: Jesse Hanner
VALUES
('e5449026-cd9a-4bed-a648-fe3ad9382832', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesses private note','${aeskey}'));

---- 2020-09-29 - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Pramukh Bagur
VALUES
('cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '6884ab96-2275-4af9-89d8-ad84254d8759', '2020-09-29 13:42:29.04' , false);

INSERT INTO action_items
(id, checkinid, createdbyid, description) -- created by: Jesse Hanner
Values('a6e2c822-feab-4c8b-b164-78158b2d4993', 'cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Action Item for Jesse Hanner','${aeskey}'));

---- 2020-09-20 - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: John Meyerin
VALUES
('1f68cfdc-0a4b-4118-b38e-d862a8b82bbb', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-09-20 11:32:29.04' , false);

---- 2020-06-20 - Active
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: John Meyerin
VALUES
('e60c3ca1-3894-4466-b418-9b743d058cc8', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-06-20 11:32:29.04' , false);

-- Suman Maroju Check-ins
---- NOW() - INTERVAL '2 days' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  '34a48fe2-7db9-4e39-927c-ffe32467df71',
  '1b4f99da-ef70-4a76-9b37-8bb783b749ad',
  '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', -- pdl: Geetika Sharma
  (CURRENT_DATE - INTERVAL '2 days') + random_workday_time(), -- 2 days ago with random time
  true
);

---- NOW() - INTERVAL '5 months' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  'e70524e2-3f81-4f94-afae-a638aca0d3b5',
  '1b4f99da-ef70-4a76-9b37-8bb783b749ad',
  '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', -- pdl: Geetika Sharma
  (CURRENT_DATE - INTERVAL '5 months') + random_workday_time(), -- 5 months ago with random time
  true
);


-- Julia Smith Check-ins
---- NOW() + INTERVAL '1 week' - Active
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  'b19a00d4-0225-412a-9456-d349ca293cdd',
  '59b790d2-fabc-11eb-9a03-0242ac130003',
  '6207b3fd-042d-49aa-9e28-dcc04f537c2d', -- pdl: Michael Kimberlin
  (CURRENT_DATE + INTERVAL '1 week') + random_workday_time(), -- 1 week from current date with random time
  false
);

---- NOW() - INTERVAL '3 months' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  '30026234-c228-48f5-aa93-53eab4b4dcef',
  '59b790d2-fabc-11eb-9a03-0242ac130003',
  '6207b3fd-042d-49aa-9e28-dcc04f537c2d', -- pdl: Michael Kimberlin
  (CURRENT_DATE - INTERVAL '3 months') + random_workday_time(), -- 3 months ago from current date with random time
  true
);


-- Unreal Ulysses Check-ins
---- 2021-02-25 - Completed
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Geetika Sharma
VALUES
('10184287-1746-4827-93fe-4e13cc0d2a6d', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2021-02-25 11:32:29.04', true);

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description) -- created by: Geetika Sharma
VALUES
('226a2ab8-03cc-4f9e-96c8-55cf187df045', '10184287-1746-4827-93fe-4e13cc0d2a6d', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s first note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description) -- created by: Geetika Sharma
VALUES
('444f6923-7b8e-4d03-8d33-021e7a72653c', '10184287-1746-4827-93fe-4e13cc0d2a6d', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s first private note for Ulysses', '${aeskey}'));

---- 2021-03-05 - Completed
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Geetika Sharma
VALUES
('bdea5de0-4358-4b33-9772-0cd953567540', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2021-03-05 11:32:29.04', true);

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description) -- created by: Geetika Sharma
VALUES
('c0d76e16-f96a-4598-8006-52b803e8b26d', 'bdea5de0-4358-4b33-9772-0cd953567540', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s second note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description) -- created by: Geetika Sharma
VALUES
('cc47b557-ed78-45c4-b577-89c1c9e705bd', 'bdea5de0-4358-4b33-9772-0cd953567540', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s second private note for Ulysses', '${aeskey}'));

---- 2022-01-16 - Completed
INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed) -- pdl: Julia Smith
VALUES
('553aa528-d5f6-4d15-bfb6-b53738dc7954', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2022-01-16 11:32:29.04', true);

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description) -- created by: Julia Smith
VALUES
('73a5e7b5-9292-45c0-a605-5b5c63230892', '553aa528-d5f6-4d15-bfb6-b53738dc7954', '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia''s first note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description) -- created by: Julia Smith
VALUES
('73a5e7b5-9292-45c0-a605-5b5c63230892', '553aa528-d5f6-4d15-bfb6-b53738dc7954', '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia''s first private note for Ulysses', '${aeskey}'));


-- Joe Warner Check-ins
---- NOW() - INTERVAL '1 day' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  'f5254f0d-97ba-4e5b-9c16-9a83bb545124',
  '066b186f-1425-45de-89f2-4ddcc6ebe237',
  '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', -- pdl: Mark Volkmann
  (CURRENT_DATE - INTERVAL '1 day') + random_workday_time(), -- 1 day ago with random time
  true
);

---- NOW() - INTERVAL '3 months' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  '962fa088-cf7d-476f-8d8d-625ce190de84',
  '066b186f-1425-45de-89f2-4ddcc6ebe237',
  '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', -- pdl: Mark Volkmann
  (CURRENT_DATE - INTERVAL '3 months') + random_workday_time(), -- 3 months ago with random time
  true
);


-- Holly Williams Check-ins
---- NOW() + INTERVAL '1 month' - Active
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  '1490caa7-1856-4c08-b287-5aa3684952e6',
  '8fa673c0-ca19-4271-b759-41cb9db2e83a',
  '802cb1f5-a255-4236-8719-773fa53d79d9', -- pdl: John Meyerin
  (CURRENT_DATE + INTERVAL '1 month') + random_workday_time(), -- 1 month from now with random time
  false
);

---- NOW() - INTERVAL '3 months' - Completed
INSERT INTO checkins (id, teammemberid, pdlid, checkindate, completed)
VALUES (
  'a41d9472-69b7-412a-9f76-8d41dae2b165',
  '8fa673c0-ca19-4271-b759-41cb9db2e83a', -- Holly Williams
  '802cb1f5-a255-4236-8719-773fa53d79d9', -- pdl: John Meyerin
  (CURRENT_DATE - INTERVAL '3 months') + random_workday_time(), -- 3 months ago with random time
  true
);


-- Guilds
insert into guild (id, name, description) -- Software Engineering
values('ba42d181-3c5b-4ee3-938d-be122c314bee',  PGP_SYM_ENCRYPT('Software Engineering','${aeskey}'), PGP_SYM_ENCRYPT('Resource for Software Engineering Topics','${aeskey}'));

insert into guild (id, name, description) -- Micronaut
values('06cd3202-a209-4ae1-a49a-10395fbe3548', PGP_SYM_ENCRYPT('Micronaut','${aeskey}'), PGP_SYM_ENCRYPT('For Micronaut Lovers and Learners','${aeskey}'));

insert into guild (id, name, description) -- Fullstack Development
values('d1d4af0e-b1a5-47eb-be49-f3581271f1e3', PGP_SYM_ENCRYPT('Fullstack Development','${aeskey}'), PGP_SYM_ENCRYPT('Full Stack Development Interests','${aeskey}'));


-- Software Engineering Guild Members
insert into guild_member (id, guildId, memberId, lead)
values('fd976615-6a8b-4cd1-8aea-cb7751c8ee1a','ba42d181-3c5b-4ee3-938d-be122c314bee', 'b2d35288-7f1e-4549-aa2b-68396b162490', true); -- Mohit Bhatia

insert into guild_member (id, guildId, memberId, lead)
values('86dc52b9-5b2a-4241-9c54-0fde07600c58','ba42d181-3c5b-4ee3-938d-be122c314bee', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', false); -- Suman Maroju

insert into guild_member (id, guildId, memberId, lead)
values('7cd12bb9-6aa4-4edc-831f-f4ebe8f22f62','ba42d181-3c5b-4ee3-938d-be122c314bee', '6884ab96-2275-4af9-89d8-ad84254d8759', false); -- Pramukh Bagur

insert into guild_member (id, guildId, memberId, lead)
values('8a20e99f-c326-4529-8024-26724a8586b1','ba42d181-3c5b-4ee3-938d-be122c314bee', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false); -- Holly Williams


-- Micronaut Guild Members
insert into guild_member (id, guildId, memberId, lead)
values('7ffe3937-bdce-4ebb-a03d-8a8b7d4703ef','06cd3202-a209-4ae1-a49a-10395fbe3548', '802cb1f5-a255-4236-8719-773fa53d79d9', true); -- John Meyerin

insert into guild_member (id, guildId, memberId, lead)
values('dd694cf2-c0f9-4470-b897-00c564c1252b','06cd3202-a209-4ae1-a49a-10395fbe3548', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false); -- Holly Williams


-- Pulse
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Holly Williams
VALUES
('802e125c-4db5-4ed9-a4a6-024cc23e4e41', '2024-05-17', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('internal #1','${aeskey}'), PGP_SYM_ENCRYPT('external #1','${aeskey}'), 4, 5);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Holly Williams
VALUES
('23dfdb75-8ef7-4754-a82a-d39e165a9aab', '2024-05-18', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('internal #2','${aeskey}'), PGP_SYM_ENCRYPT('external #2','${aeskey}'), 3, 4);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Holly Williams
VALUES
('a11b7f6b-aaa4-4a3e-a0a3-48da0a31e695', '2024-05-19', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('internal #3','${aeskey}'), PGP_SYM_ENCRYPT('external #3','${aeskey}'), 2, 5);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Holly Williams
VALUES
('cda41eed-70ea-4d3f-a9d7-cd0c5158eb5f', '2024-05-20', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('internal #4','${aeskey}'), PGP_SYM_ENCRYPT('external #4','${aeskey}'), 4, 1);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Holly Williams
VALUES
('c75be148-1cb6-425f-9671-7fb68a33f2bf', '2024-05-21', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('internal #5','${aeskey}'), PGP_SYM_ENCRYPT('external #5','${aeskey}'), 5, 2);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Mark Volkmann
VALUES
('8c4e215f-44c8-4b73-bdd1-b5cdc0048f32', '2024-05-21', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d',  PGP_SYM_ENCRYPT('working on Check-ins','${aeskey}'), PGP_SYM_ENCRYPT('writing htmx book','${aeskey}'), 3, 5);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Pramukh Bagur
VALUES
('01adf25c-84ee-4421-a9cc-513298d4cc25', '2024-05-21', '6884ab96-2275-4af9-89d8-ad84254d8759',  PGP_SYM_ENCRYPT('internal #6','${aeskey}'), PGP_SYM_ENCRYPT('extenal #6','${aeskey}'), 1, 2);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- John Meyerin
VALUES
('e7549dce-811c-41fa-be2c-188f6edefe06', '2024-05-21', '802cb1f5-a255-4236-8719-773fa53d79d9',  PGP_SYM_ENCRYPT('internal #7','${aeskey}'), PGP_SYM_ENCRYPT('extenal #7','${aeskey}'), 2, 3);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Mohit Bhatia
VALUES
('251a6c5d-a1fd-4967-aa8a-691f0e35569f', '2024-05-21', 'b2d35288-7f1e-4549-aa2b-68396b162490',  PGP_SYM_ENCRYPT('internal #8','${aeskey}'), PGP_SYM_ENCRYPT('extenal #8','${aeskey}'), 3, 4);
INSERT INTO pulse_response
(id, submissiondate, teammemberid, internalfeelings, externalfeelings, internal_score, external_score) -- Suman Maroju
VALUES
('fa2319a4-8db6-4b83-bba7-70a3e4d7670f', '2024-05-21', '1b4f99da-ef70-4a76-9b37-8bb783b749ad',  PGP_SYM_ENCRYPT('internal #9','${aeskey}'), PGP_SYM_ENCRYPT('extenal #9','${aeskey}'), 4, 5);

-- Admin Permissions
insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_ORGANIZATION_MEMBERS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_DELETE_ORGANIZATION_MEMBERS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_DELETE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_FEEDBACK_ANSWER');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_ROLE_PERMISSIONS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_ASSIGN_ROLE_PERMISSIONS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_PERMISSIONS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_SKILLS_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_RETENTION_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_ANNIVERSARY_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_BIRTHDAY_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_PROFILE_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_CHECKINS_REPORT');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_EDIT_SKILL_CATEGORIES');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_SKILL_CATEGORIES');

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_PRIVATE_NOTE');  

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_PRIVATE_NOTE'); 

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_PRIVATE_NOTE'); 

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_CHECKIN_DOCUMENT'); 

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_CHECKIN_DOCUMENT'); 

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_CHECKIN_DOCUMENT'); 

insert into role_permissions
    (roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_DELETE_CHECKIN_DOCUMENT'); 

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_ALL_CHECKINS'); 

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_ALL_CHECKINS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_REVIEW_ASSIGNMENTS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_REVIEW_ASSIGNMENTS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_REVIEW_ASSIGNMENTS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_DELETE_REVIEW_ASSIGNMENTS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_ADMINISTER_SETTINGS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_SETTINGS');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CREATE_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_UPDATE_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_LAUNCH_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_CLOSE_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_DELETE_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'CAN_VIEW_ALL_PULSE_RESPONSES');

-- PDL Permissions
insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_CREATE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_DELETE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_FEEDBACK_ANSWER');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_PERMISSIONS');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_CREATE_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_UPDATE_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_CREATE_PRIVATE_NOTE');  

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_PRIVATE_NOTE'); 

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_UPDATE_PRIVATE_NOTE'); 

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_CREATE_CHECKIN_DOCUMENT'); 

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_CHECKIN_DOCUMENT'); 

insert into role_permissions
    (roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_UPDATE_CHECKIN_DOCUMENT');

insert into role_permissions
(roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_VIEW_REVIEW_PERIOD');

insert into role_permissions
(roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_MANAGE_CERTIFICATIONS');

insert into role_permissions
(roleid, permission)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'CAN_MANAGE_EARNED_CERTIFICATIONS');

-- Member permissions
insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_VIEW_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_CREATE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_DELETE_FEEDBACK_REQUEST');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_VIEW_FEEDBACK_ANSWER');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_VIEW_PERMISSIONS');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_VIEW_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_CREATE_CHECKINS');

insert into role_permissions
    (roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_UPDATE_CHECKINS');

insert into role_permissions
(roleid, permission)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'CAN_VIEW_REVIEW_PERIOD');


-- Feedback Templates
---- Quarter 1 Feedback Template
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Michael Kimberlin
VALUES
('18ef2032-c264-411e-a8e1-ddda9a714bae', 'Q1 Feedback', 'Get feedback for quarter 1', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2021-06-06', true, true, false);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('d6d05f53-682c-4c37-be32-8aab5f89767f', PGP_SYM_ENCRYPT('What are this team member''s top strengths (include examples where possible)?','${aeskey}'), '18ef2032-c264-411e-a8e1-ddda9a714bae', 1, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('47f997ca-0045-4147-afcb-0c9ed0b44978', PGP_SYM_ENCRYPT('In what ways are this team member''s contributions impacting the objectives of the organization, their project, or their team?','${aeskey}'), '18ef2032-c264-411e-a8e1-ddda9a714bae', 2, 'TEXT');

---- Generic Survey 1
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Gina Bremehr
VALUES
('97b0a312-e5dd-46f4-a600-d8be2ad925bb', 'Survey 1', 'Make a survey with a few questions', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2021-05-05', true, true, false);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('89c8b612-fca8-4144-88cd-176ddfca35ad', PGP_SYM_ENCRYPT('What can this team member improve on that would help them increase their effectiveness (include examples where possible)?','${aeskey}'), '97b0a312-e5dd-46f4-a600-d8be2ad925bb', 1, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('3571cf89-22b9-4e0e-baff-1a1e45482472', PGP_SYM_ENCRYPT('Try to recall a time when this team member helped you out with something. What was the problem and how did you work together to solve it?','${aeskey}'), '97b0a312-e5dd-46f4-a600-d8be2ad925bb', 3, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('afa7e2cb-366a-4c16-a205-c0d493b80d85', PGP_SYM_ENCRYPT('In what ways does this team member represent OCI values?','${aeskey}'), '97b0a312-e5dd-46f4-a600-d8be2ad925bb', 2, 'TEXT');

---- Generic Mulitple Choice Survey
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Gina Bremehr
VALUES
('1c8bc142-c447-4889-986e-42ab177da683', 'Multiple Choice Survey', 'This survey contains radio buttons and sliders.', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2022-04-04', true, true, false);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('22113310-04dd-4931-96f2-37303a2515a4', PGP_SYM_ENCRYPT('Does this team member regularly attend meetings?', '${aeskey}'), '1c8bc142-c447-4889-986e-42ab177da683', 1, 'RADIO');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('11d7b14c-2eee-4f72-a2b6-8c57a094207e', PGP_SYM_ENCRYPT('Would you say that this team member demonstrates high productivity?', '${aeskey}'), '1c8bc142-c447-4889-986e-42ab177da683', 2, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('bf328e35-e486-4ec8-b3e8-acc2c09419fa', PGP_SYM_ENCRYPT('Feel free to elaborate on the choices you made above if you choose to do so', '${aeskey}'), '1c8bc142-c447-4889-986e-42ab177da683', 3, 'TEXT');

---- Empty Template
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Ron Steinkamp
VALUES
('2cb80a06-e723-482f-af9b-6b9516cabfcd', 'Empty Template', 'This template does not have any questions on it', '2559a257-ae84-4076-9ed4-3820c427beeb', '2020-04-04', true, true, false);

---- Private Template
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Geetika Sharma
VALUES
('492e4f61-c7e3-4c30-a650-7ec74f2ba545', 'Private Template', 'This template is private', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2020-06-07', true, false, false);

---- Private Template 2
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc) -- created by: Geetika Sharma
VALUES
('c5d10880-f561-11eb-9a03-0242ac130003', 'Private Template 2', 'This template is private', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2020-06-10', true, false, false);

---- Self Review - 2022
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc, is_review) -- created by: Gina Bremehr
VALUES
('926a37a4-4ded-4633-8900-715b0383aecc', 'Self Review - 2022', 'This survey is intended for performance self-review.', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2022-11-01', true, true, false, true);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('f4a394de-bcc0-40ad-9b86-3fa7bd6c09fe', PGP_SYM_ENCRYPT('My performance (contributions, knowledge, & skill) was strong during the period covered by this review.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 1, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('2ffa7f2d-3d83-4ffe-b4f2-e8fc10e77170', PGP_SYM_ENCRYPT('How often did you display each of the following behaviors during the period covered by this review?', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 2, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('6ea9ea69-62ba-4835-b2b3-43d565df209f', PGP_SYM_ENCRYPT('Leadership - Team members who display leadership are supportive, conscientious, and empathetic. They provide psychological safety and inspire others to greater levels of contribution.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 3, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('aa58579c-3d6c-4238-a3bd-e3f904584f3f', PGP_SYM_ENCRYPT('Clear and Timely Communication - Team members who display clear and timely communication provide the necessary level of detail in their communication, target their messaging (tone and recipients), and modulate timing and urgency appropriately.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 4, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('611536b3-2275-4509-92db-0372bac60aff', PGP_SYM_ENCRYPT('Problem-Solving - Team members who display problem-solving can respond to client, project, or other challenges without the need for intervention. They show the ability to think outside of the box about processes or tasks to remove obstacles and improve efficiency.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 5, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('68549286-76f3-49f6-9de6-5f96abfa0b0e', PGP_SYM_ENCRYPT('Flexibility - Team members who display flexibility modify their approach to tasks or people based on the needs of the situation. They exhibit the ability to change course or alter behavior when new information is obtained, or the desired results are not being achieved.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 6, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('d77848f3-cb94-4161-b558-7aa230dcc92c', PGP_SYM_ENCRYPT('Dependability - Team members who display dependability exhibit appropriate attention to detail and are punctual. They meet deadlines and agreements and take initiative when needed.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 7, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('b07d06b6-8d6c-4c39-88f9-b3ccc7a691c4', PGP_SYM_ENCRYPT('Commitment to Business - Team members who display a commitment to business take ownership of assigned tasks, projects, and areas of responsibility. They look for opportunities to grow and improve the organization’s business and culture.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 8, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('a41e17ff-a3ed-4cfa-a98a-67726390d26c', PGP_SYM_ENCRYPT('You can provide more detailed context to your self-review below. Be as specific as possible!', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 9, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('82be76a8-0cf8-427d-9d6a-763c23c05db2', PGP_SYM_ENCRYPT('What significant accomplishments, contributions, or examples of displaying the OCI Values have you had during the period covered by this review? (Completed projects, guild participation, tech talks, SETT articles, volunteer activities, etc.)', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 10, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('f9e5878c-6c4d-4249-8f1a-c3508d8c1597', PGP_SYM_ENCRYPT('Please provide any additional context or reasoning relevant to your self-assessment.', '${aeskey}'), '926a37a4-4ded-4633-8900-715b0383aecc', 11, 'TEXT');

---- Annual Review - 2022
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc, is_review) -- created by: Gina Bremehr
VALUES
('d1e94b60-47c4-4945-87d1-4dc88f088e57', 'Annual Review - 2022', 'This survey is intended for performance review.', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2022-11-21', true, true, false, true);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('6eff224d-d690-4d25-a44f-08b8ec03fbbe', PGP_SYM_ENCRYPT('This team member''s performance (contributions, knowledge, & skill) was strong during the period covered by this review.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 1, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('47758dfb-64ca-4203-a5ba-b5fa3ef254dd', PGP_SYM_ENCRYPT('This team member''s potential to contribute is high.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 2, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('69576f2e-0236-4401-96f7-2058dfd8759f', PGP_SYM_ENCRYPT('How often has this team member displayed each of the following behaviors during the period covered by this review?', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 3, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('4ab44c4f-bd8f-4e8d-89b7-940ada1a45c0', PGP_SYM_ENCRYPT('Leadership - Team members who display leadership are supportive, conscientious, and empathetic. They provide psychological safety and inspire others to greater levels of contribution.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 4, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('1fe655dc-9c37-4e55-9bd9-455059832531', PGP_SYM_ENCRYPT('Clear and Timely Communication - Team members who display clear and timely communication provide the necessary level of detail in their communication, target their messaging (tone and recipients), and modulate timing and urgency appropriately.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 5, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('cf00f422-0b8a-4219-bc3a-8092732f2ef5', PGP_SYM_ENCRYPT('Problem-Solving - Team members who display problem-solving can respond to client, project, or other challenges without the need for intervention. They show the ability to think outside of the box about processes or tasks to remove obstacles and improve efficiency.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 6, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('3f74c7fe-74fe-4a4d-96c3-aaa35d0cbe70', PGP_SYM_ENCRYPT('Flexibility - Team members who display flexibility modify their approach to tasks or people based on the needs of the situation. They exhibit the ability to change course or alter behavior when new information is obtained, or the desired results are not being achieved.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 7, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('40448b19-7d3a-4862-aa97-506e768ca4f9', PGP_SYM_ENCRYPT('Dependability - Team members who display dependability exhibit appropriate attention to detail and are punctual. They meet deadlines and agreements and take initiative when needed.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 8, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('7fad2952-fa1a-43eb-86e6-9a715674c884', PGP_SYM_ENCRYPT('Commitment to Business - Team members who display a commitment to business take ownership of assigned tasks, projects, and areas of responsibility. They look for opportunities to grow and improve the organization’s business and culture.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 9, 'FREQ');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('f40b00b7-4631-4a08-a4a7-a41b7497ddb8', PGP_SYM_ENCRYPT('You can provide more detailed context to your review of this team member below. Be as specific as possible!', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 10, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('93424f36-64a3-4f10-b78a-00de58060177', PGP_SYM_ENCRYPT('What significant accomplishments, contributions, or examples of displaying the OCI Values has this team member had during the period covered by this review?', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 11, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('8c13c1a5-f1ef-43cc-9f9a-858b01bff930', PGP_SYM_ENCRYPT('This team member should be considered for a promotion.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 12, 'RADIO');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('c2ff8a0d-358f-438b-86f4-59da10bddbe5', PGP_SYM_ENCRYPT('This team member is at risk for low performance.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 13, 'RADIO');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('46cf546a-acbe-48e5-8c8d-1b1ca484af8d', PGP_SYM_ENCRYPT('This team member should be considered for a leadership track.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 14, 'RADIO');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('174e5851-cb24-4a0f-890c-e6f041db4127', PGP_SYM_ENCRYPT('Please provide any additional context or reasoning relevant to your assessment of this team member.', '${aeskey}'), 'd1e94b60-47c4-4945-87d1-4dc88f088e57', 15, 'TEXT');


-- Feedback Requests without responses
---- Creator: Geetika Sharma
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Suman Maroju, recipient: Jesse Hanner
VALUES
('d62b5c09-7ff9-4b0a-bfee-7f467470a7ef', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '18ef2032-c264-411e-a8e1-ddda9a714bae', '2020-07-07', '2021-08-01', null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Mohit Bhatia, recipient: Jesse Hanner
VALUES
('ab7b21d4-f88c-4494-9b0b-8541636025eb', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Mohit Bhatia, recipient: Zack Brown
VALUES
('2dd2347a-c296-4986-b428-3fbf6a24ea1e', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Mohit Bhatia, recipient: Mark Volkmann
VALUES
('c15961e4-6e9b-42cd-8140-ece9efe2445c', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498' , 'b2d35288-7f1e-4549-aa2b-68396b162490', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, '2020-07-08', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Holly Williams, recipient: Pramukh Bagur
VALUES
('e2e24336-0615-4564-af29-d0f7b3ac3db9', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '8fa673c0-ca19-4271-b759-41cb9db2e83a', '6884ab96-2275-4af9-89d8-ad84254d8759', '18ef2032-c264-411e-a8e1-ddda9a714bae', '2018-12-24', '2018-12-25', null, 'sent');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Mohit Bhatia, recipient: Gina Bremehr
VALUES
('09fbdaf2-f554-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9','97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, '2020-07-07', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Mohit Bhatia, recipient: Gina Bremehr
VALUES
('82d9db7c-f554-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9','97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-05', null, '2020-07-10', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Holly Williams, recipient: Zack Brown
VALUES
('e2af1c96-a593-48c2-b9e0-a00193a070c7', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '8fa673c0-ca19-4271-b759-41cb9db2e83a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-08-01', '2021-08-05', '2021-08-02', 'submitted');

---- Creator: Gina Bremehr
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Ron Steinkamp
VALUES
('a50f2f8a-7eb0-4456-b5ef-382086827ba0', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '2559a257-ae84-4076-9ed4-3820c427beeb', '1c8bc142-c447-4889-986e-42ab177da683', '2022-04-14', null, null, 'sent');

---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Jesse Hanner
VALUES
('ab2da7fc-fac2-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3' ,'18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-08-01', '2021-08-05', null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Zach Brown, recipient: Michael Kimberlin
VALUES
('ab2da7fc-fac2-11eb-9a03-0242ac130004', '59b790d2-fabc-11eb-9a03-0242ac130003', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '6207b3fd-042d-49aa-9e28-dcc04f537c2d' ,'18ef2032-c264-411e-a8e1-ddda9a714bae', (NOW())::date, (NOW() + INTERVAL '1 DAY')::date, null, 'pending');
---- Creator: Zack Brown
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Zack Brown
VALUES
('0d0d872d-4f05-4af8-9804-d0a99e450c37', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2022-03-01', '2023-08-05', '2022-04-01', 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Zack Brown
VALUES
('1aff4993-2324-41cc-8b21-2ab5715ca70b', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2022-03-01', '2023-08-05', '2022-04-01', 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Zack Brown
VALUES
('7ca4d402-0bb9-4989-9087-8a52a63ee5d0', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2022-03-01', '2023-08-05', '2022-04-01', 'pending');


-- Feedback Requests with responses
---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Zack Brown
VALUES
('d09031be-fac1-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2020-08-01', '2020-08-05', '2020-08-02', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('dbd2da2b-df0a-4e11-9fcd-ed0774a5fdea', PGP_SYM_ENCRYPT('They have strong engineering and verbal skills, but could be better at being tactful with client requests..','${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f', 'd09031be-fac1-11eb-9a03-0242ac130003', 0.5);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('766a3a2c-88de-4487-b285-e3c667ffe0e6', PGP_SYM_ENCRYPT('While they do a good job of innovating courageously, like I said, their presence sometimes impacts client relations.','${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', 'd09031be-fac1-11eb-9a03-0242ac130003', -0.2);

---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Suman Maroju
VALUES
('b5596a80-fac3-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '1b4f99da-ef70-4a76-9b37-8bb783b749ad','18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-02-15', '2021-02-25', '2021-02-20', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('94550858-facd-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('They have done a great job on this project, but could have spoken to the client a litle more about their options.', '${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f', 'b5596a80-fac3-11eb-9a03-0242ac130003', 0.7);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('98e0c286-facd-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('I have few complaints except the aforementioned need to communicate more with the client', '${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', 'b5596a80-fac3-11eb-9a03-0242ac130003', 0.2);

---- Creator: Zack Brown
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Faux Freddy, recipient: Zack Brown
VALUES
('74623897-5279-4dbe-94d4-5a247d9f00b1', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2022-03-01', '2023-08-05', '2022-04-01', 'pending');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('4cbd9576-e580-4da7-8488-d1f75477f5fb', PGP_SYM_ENCRYPT('Brilliant feedback', '${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f', '74623897-5279-4dbe-94d4-5a247d9f00b1', 0.8);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('01565a0e-b8ea-486f-af2e-821a74519953', PGP_SYM_ENCRYPT('Excellent feedback', '${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', '74623897-5279-4dbe-94d4-5a247d9f00b1', 0.7);

---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Unreal Ulysses, recipient: Zack Brown
VALUES
('b1f60cfa-fac2-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-07-22', '2021-07-31', '2021-07-29', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('b481d3b2-face-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('I like their gumption!!', '${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f','b1f60cfa-fac2-11eb-9a03-0242ac130003', 0.7);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('c38e5fba-face-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('They are very fun to work with :)', '${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', 'b1f60cfa-fac2-11eb-9a03-0242ac130003', 0.8);

---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Unreal Ulysses, recipient: Zack Brown
VALUES
('e238dd00-fac4-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2021-03-22', '2021-04-01', '2021-04-01', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('87f11eb4-fad0-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('This team member could do better with their understanding of React', '${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f','e238dd00-fac4-11eb-9a03-0242ac130003', 0.3);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('0c819e0e-e237-4759-9967-550a3462e516', PGP_SYM_ENCRYPT('They sometimes struggle with writing services using Micronaut.', '${aeskey}'), '89c8b612-fca8-4144-88cd-176ddfca35ad', 'e238dd00-fac4-11eb-9a03-0242ac130003', 0.4);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('5a65fe6b-0f27-4d2c-bc25-a637bc33d630', PGP_SYM_ENCRYPT('Nothing comes to mind.', '${aeskey}'), '3571cf89-22b9-4e0e-baff-1a1e45482472', 'e238dd00-fac4-11eb-9a03-0242ac130003', 0.5);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('8c13ffa2-fad0-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('They are very good at working on a team--all of us is better than any one of us', '${aeskey}'), 'afa7e2cb-366a-4c16-a205-c0d493b80d85', 'e238dd00-fac4-11eb-9a03-0242ac130003', 0.8);

---- Creator: Julia Smith
INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status) -- requestee: Unreal Ulysses, recipient: Joe Warner
VALUES
('4240735d-15fd-4eea-8bca-8c642a433036', '59b790d2-fabc-11eb-9a03-0242ac130003', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '066b186f-1425-45de-89f2-4ddcc6ebe237', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2021-03-22', '2021-04-01', '2021-04-01', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('d19e6ac1-f081-414c-a51a-ccc684131bec', PGP_SYM_ENCRYPT('They could definitely learn how to use Vim better. They keep asking me how to exit!', '${aeskey}'), '89c8b612-fca8-4144-88cd-176ddfca35ad', '4240735d-15fd-4eea-8bca-8c642a433036', 0.3);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('603b1308-6cc4-4534-b588-921b7b3e476d', PGP_SYM_ENCRYPT('There was one time where I could not figure out the cause of a bug. They showed me how to use the debugging software to locate the bug, which was extremely helpful. I was able to quickly patch the bug after that.', '${aeskey}'), '3571cf89-22b9-4e0e-baff-1a1e45482472', '4240735d-15fd-4eea-8bca-8c642a433036', 0.9);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('a223135a-742b-45c6-b9a4-2bb990d956b2', PGP_SYM_ENCRYPT('They are always punctual, and work well with the other members of the team. Although they have a few technical skills they could brush up on, our team is lucky to have them.', '${aeskey}'), 'afa7e2cb-366a-4c16-a205-c0d493b80d85', '4240735d-15fd-4eea-8bca-8c642a433036', 0.7);


-- Skills
INSERT INTO skills -- React
(id, name, pending, description, extraneous)
VALUES
('f057af45-e627-499c-8a71-1e6b4ab2fcd2', 'React', false, 'Component-based JavaScript framework', false);

INSERT INTO skills -- Micronaut
(id, name, pending, description, extraneous)
VALUES
('689bb262-10af-40ef-bbf6-d8ad062e1470', 'Micronaut', false, 'JVM framework for microservices and serverless apps', false);

INSERT INTO skills -- CSS
(id, name, pending, description, extraneous)
VALUES
('6b56f0aa-09aa-4b09-bb81-03481af7e49f', 'CSS', true, 'Style sheet language', false);

INSERT INTO skills -- Git
(id, name, pending, description, extraneous)
VALUES
('84682de9-85a7-4bf7-b74b-e9054311a61a', 'Git', true, 'Version control system', false);

INSERT INTO skills -- Java
(id, name, pending, description, extraneous)
VALUES
('06c03df3-85fe-4fc3-979e-9f1f6ba74a03', 'Java', false, 'Object-oriented programming language', false);

-- Member Skills
INSERT INTO member_skills -- Big Boss, React
(id, memberid, skillid, skilllevel, lastuseddate)
VALUES
('99b7b700-bba3-440b-8df5-c1b668e9e7e0', '72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', 'f057af45-e627-499c-8a71-1e6b4ab2fcd2', '5', '2022-06-01');

INSERT INTO member_skills -- Big Boss, CSS
(id, memberid, skillid, skilllevel, lastuseddate)
VALUES
('daad16fa-2268-4e72-a2ad-e13aa8b8665b', '72655c4f-1fb8-4514-b31e-7f7e19fa9bd7', '6b56f0aa-09aa-4b09-bb81-03481af7e49f', '4', '2022-06-01');

INSERT INTO member_skills -- Revolver Ocelot, React
(id, memberid, skillid, skilllevel, lastuseddate)
VALUES
('e2de59a8-71be-4972-86be-608538503195', '105f2968-a182-45a3-892c-eeff76383fe0', 'f057af45-e627-499c-8a71-1e6b4ab2fcd2', '3', '2022-05-01');

INSERT INTO member_skills -- Faux Freddy, Java
(id, memberid, skillid, skilllevel, lastuseddate)
VALUES
    ('722c3545-4f5d-459a-b66c-3ff98d5de11b', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '06c03df3-85fe-4fc3-979e-9f1f6ba74a03', '2', '2022-07-01');

INSERT INTO member_skills -- Revolver Ocelot, Java
(id, memberid, skillid, skilllevel, lastuseddate)
VALUES
    ('d27b679c-3aa8-4c4d-b08e-4eda63cea23f', '105f2968-a182-45a3-892c-eeff76383fe0', '06c03df3-85fe-4fc3-979e-9f1f6ba74a03', '5', '2022-08-01');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('39dfd281-d0af-4016-848b-8156dfef2b92', PGP_SYM_ENCRYPT('Kudos to Mark Volkmann!', '${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', null, '2022-10-01', '2022-10-02');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('ebc023e1-b577-4b02-a2fb-fc9472a8474a', '39dfd281-d0af-4016-848b-8156dfef2b92', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('fbcde196-7703-4f80-ac5e-5ac60b28555e', PGP_SYM_ENCRYPT('Kudos to Geetika and Jesse!', '${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', null, '2022-10-01', null);

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('1ed43248-e50a-42b5-9435-f8f46dba92d8', 'fbcde196-7703-4f80-ac5e-5ac60b28555e', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('bf37c244-f478-42b2-9077-dd4cb052bbb1', 'fbcde196-7703-4f80-ac5e-5ac60b28555e', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('df2766f0-efab-4925-859e-d993e2e38eaa', PGP_SYM_ENCRYPT('Kudos to the Checkins Experts team!', '${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '2022-09-28', '2022-09-29');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('ab140091-4324-4994-98e0-878cadfaf177', 'df2766f0-efab-4925-859e-d993e2e38eaa', '802cb1f5-a255-4236-8719-773fa53d79d9');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('d6ea00f5-1ec7-4f38-8e53-09efd6e50c4e', 'df2766f0-efab-4925-859e-d993e2e38eaa', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('0ca86d55-cffb-4b84-883c-c4c2e41eff73', 'df2766f0-efab-4925-859e-d993e2e38eaa', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('17e19f16-d731-4242-a74a-43493a556f08', PGP_SYM_ENCRYPT('Kudos to Michael Kimberlin!', '${aeskey}'), '6884ab96-2275-4af9-89d8-ad84254d8759', null, '2022-08-15', '2022-08-25');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('038485ee-8cac-416b-9da6-d838ee345d0e', '17e19f16-d731-4242-a74a-43493a556f08', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('6612b8a0-1d45-4155-b0fb-e0b2d9eaafcb', PGP_SYM_ENCRYPT('Wanted to give some kudos to Michael Kimberlin and Mark Volkmann for their recent help on the project. Thank you very much!', '${aeskey}'), '1b4f99da-ef70-4a76-9b37-8bb783b749ad', null, '2022-10-05', null);

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('59d99cdf-30c5-47c7-988a-42f6602f4cd4', '6612b8a0-1d45-4155-b0fb-e0b2d9eaafcb', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('01f639d5-b7b3-47a2-b165-d7a20d01fec2', '6612b8a0-1d45-4155-b0fb-e0b2d9eaafcb', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO kudos
(id, message, senderid, teamid, datecreated, dateapproved)
VALUES
('9cdce399-4c02-41ed-a63f-35beb6ecb622', PGP_SYM_ENCRYPT('A huge thank you to the Javascript Gurus team for offering their advice on the tech stack for the new project. Kudos to you all!', '${aeskey}'), '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', '2022-10-10', '2022-10-11');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('de84b3fb-f79f-40fa-a4e6-d5126140f3ea', '9cdce399-4c02-41ed-a63f-35beb6ecb622', '43ee8e79-b33d-44cd-b23c-e183894ebfef');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('ec98ebba-c34b-428b-b377-b38a1b882fe6', '9cdce399-4c02-41ed-a63f-35beb6ecb622', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO kudos_recipient
(id, kudosid, memberid)
VALUES
('f792056b-22ce-4e3d-a442-0fdc3cb35e7b', '9cdce399-4c02-41ed-a63f-35beb6ecb622', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

-- Skill Categories
INSERT INTO skillcategories
(id, name, description)
VALUES
('38cd877f-1c50-4167-a592-eed3847ebf0b', 'Languages', 'Programming languages');

INSERT INTO skillcategories
(id, name, description)
VALUES
('ad2a7de0-aad8-4bde-837a-566aaef1fa7c', 'Essential Skills', 'General skills useful for the workplace');

INSERT INTO skillcategories
(id, name, description)
VALUES
('0778a8e7-21d8-4ca3-a0dc-cad676aac417', 'Tools', 'Software tools');

-- SkillCategory
INSERT INTO skillcategory_skills -- Languages CSS
(skillcategory_id, skill_id)
values
('38cd877f-1c50-4167-a592-eed3847ebf0b', '6b56f0aa-09aa-4b09-bb81-03481af7e49f');

INSERT INTO skillcategory_skills -- Tools GIT
(skillcategory_id, skill_id)
values
('0778a8e7-21d8-4ca3-a0dc-cad676aac417', '84682de9-85a7-4bf7-b74b-e9054311a61a');

INSERT INTO skillcategory_skills -- Tools CSS
(skillcategory_id, skill_id)
values
('0778a8e7-21d8-4ca3-a0dc-cad676aac417', '6b56f0aa-09aa-4b09-bb81-03481af7e49f');

--- CERTIFICATIONS

INSERT INTO certification
    (certification_id, name, badge_url)
VALUES
    ('23b248e1-40f3-4477-b1b6-544b743e6ee3', 'Java', 'https://images.credly.com/images/235d5b25-d41e-48c2-9c0e-63b373e78fc8/image.png');

INSERT INTO certification
    (certification_id, name, badge_url)
VALUES
    ('68343978-4072-4b48-aa9c-01f7ec910c9b', 'Python', 'https://pythoninstitute.org/assets/61f11f7719dd3800707549.png');

--- MEMBER CERTIFICATIONS

INSERT INTO earned_certification
    (earned_certification_id, member_id, certification_id, description, earned_date)
VALUES -- Michael Kimberlin, Java
    ('d946dfaa-4bae-4a4e-a3c3-9378ce1cae37', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '23b248e1-40f3-4477-b1b6-544b743e6ee3', 'Java certification', '2024-04-01');

INSERT INTO earned_certification
    (earned_certification_id, member_id, certification_id, description, earned_date)
VALUES -- Mark Volkmann, Java
    ('42471a8c-8851-42a0-8cc2-bc42cb1020cc', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '23b248e1-40f3-4477-b1b6-544b743e6ee3', 'Java certification', '2022-06-01');

INSERT INTO earned_certification
    (earned_certification_id, member_id, certification_id, description, earned_date)
VALUES -- Mark Volkmann, Python
    ('1f4272da-6ecb-4c15-b4a8-28739405bd1c', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '68343978-4072-4b48-aa9c-01f7ec910c9b', 'Python certification', '2024-03-01');
