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
delete from role_permissions;
delete from role;
delete from team_member;
delete from team;
delete from feedback_answers;
delete from feedback_requests;
delete from template_questions;
delete from feedback_templates;
delete from member_profile;

INSERT INTO member_profile
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate)
VALUES
('01b7d769-9fa2-43ff-95c7-f3b950a27bf9', PGP_SYM_ENCRYPT('Gina','${aeskey}'), PGP_SYM_ENCRYPT('Bremehr','${aeskey}'), PGP_SYM_ENCRYPT('COO','${aeskey}'), PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bremehrg@objectcomputing.com','${aeskey}'), '12312345', '2012-09-20', PGP_SYM_ENCRYPT('Epitome of Strong Woman','${aeskey}'), null, '1988-09-21');

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate)
VALUES
('2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('Ron','${aeskey}'), PGP_SYM_ENCRYPT('Steinkamp','${aeskey}'), PGP_SYM_ENCRYPT('Senior Project Manager','${aeskey}'), '01b7d769-9fa2-43ff-95c7-f3b950a27bf9',  PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('steinkampr@objectcomputing.com','${aeskey}'), '12312346', '2012-09-29', PGP_SYM_ENCRYPT('Managing projects well','${aeskey}'), null, '1988-09-02');

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('John','${aeskey}'), PGP_SYM_ENCRYPT('Meyerin','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('meyerinj@objectcomputing.com','${aeskey}'), '12312347', '2012-09-29', PGP_SYM_ENCRYPT('Outstanding Engineer','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika','${aeskey}'), PGP_SYM_ENCRYPT('Sharma','${aeskey}'), PGP_SYM_ENCRYPT('PMO Administrator','${aeskey}'), '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('sharmag@objectcomputing.com','${aeskey}'), '12312348', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Wrangler Extrodinaire','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Holly','${aeskey}'), PGP_SYM_ENCRYPT('Williams','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('williamsh@objectcomputing.com','${aeskey}'), '12312349', '2012-09-29', PGP_SYM_ENCRYPT('Software Engineer Remarkable','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('6207b3fd-042d-49aa-9e28-dcc04f537c2d', PGP_SYM_ENCRYPT('Michael','${aeskey}'), PGP_SYM_ENCRYPT('Kimberlin','${aeskey}'), PGP_SYM_ENCRYPT('Director of Organizational Development','${aeskey}'), '8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('kimberlinm@objectcomputing.com','${aeskey}'), '12312342', '2012-09-29', PGP_SYM_ENCRYPT('Developer of developers and others','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', PGP_SYM_ENCRYPT('Mark','${aeskey}'), PGP_SYM_ENCRYPT('Volkmann','${aeskey}'), PGP_SYM_ENCRYPT('Partner and Distinguished Engineer','${aeskey}'), PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('volkmannm@objectcomputing.com','${aeskey}'), '12312343', '2012-09-29', PGP_SYM_ENCRYPT('Software Engineer Spectacular','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('Pramukh','${aeskey}'), PGP_SYM_ENCRYPT('Bagur','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '802cb1f5-a255-4236-8719-773fa53d79d9', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bagurp@objectcomputing.com','${aeskey}'), '12312344', '2012-09-29', PGP_SYM_ENCRYPT('Top notch Engineer','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesse','${aeskey}'), PGP_SYM_ENCRYPT('Hanner','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('hannerj@objectcomputing.com','${aeskey}'), '123123450', '2012-09-29', PGP_SYM_ENCRYPT('Amazing Engineer','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('1b4f99da-ef70-4a76-9b37-8bb783b749ad', PGP_SYM_ENCRYPT('Suman','${aeskey}'), PGP_SYM_ENCRYPT('Maroju','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('marojus@objectcomputing.com','${aeskey}'), '123123410', '2012-09-29', PGP_SYM_ENCRYPT('Superior Engineer','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('b2d35288-7f1e-4549-aa2b-68396b162490', PGP_SYM_ENCRYPT('Mohit','${aeskey}'), PGP_SYM_ENCRYPT('Bhatia','${aeskey}'), PGP_SYM_ENCRYPT('Principal Software Engineer','${aeskey}'), '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('bhatiam@objectcomputing.com','${aeskey}'), '123123411', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Extraordinaire','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('43ee8e79-b33d-44cd-b23c-e183894ebfef', PGP_SYM_ENCRYPT('Zack','${aeskey}'), PGP_SYM_ENCRYPT('Brown','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2559a257-ae84-4076-9ed4-3820c427beeb', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('brownz@objectcomputing.com','${aeskey}'), '123123412', '2012-09-29', PGP_SYM_ENCRYPT('Engineer Phenomenal','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid)
VALUES
('066b186f-1425-45de-89f2-4ddcc6ebe237', PGP_SYM_ENCRYPT('Joe','${aeskey}'), PGP_SYM_ENCRYPT('Warner','${aeskey}'), PGP_SYM_ENCRYPT('Software Engineer','${aeskey}'), '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('warnerj@objectcomputing.com','${aeskey}'), '123123413', '2012-09-29', PGP_SYM_ENCRYPT('Engineer of Supreme Ability','${aeskey}'), null);

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate)
VALUES
('59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia','${aeskey}'),  PGP_SYM_ENCRYPT('Smith','${aeskey}'), PGP_SYM_ENCRYPT('Intern','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('smithj@objectcomputing.com','${aeskey}'), '010101010', '2021-05-22', PGP_SYM_ENCRYPT('Local creature in discovery room','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1998-07-07');

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate)
VALUES
('2dee821c-de32-4d9c-9ecb-f73e5903d17a', PGP_SYM_ENCRYPT('Faux','${aeskey}'),  PGP_SYM_ENCRYPT('Freddy','${aeskey}'), PGP_SYM_ENCRYPT('Test Engineer','${aeskey}'), '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('testing@objectcomputing.com','${aeskey}'), '010101011', '2021-05-22', PGP_SYM_ENCRYPT('Test user 1','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1950-01-01');

INSERT INTO member_profile
(id, firstName, lastName, title, pdlid, location, workEmail, employeeid, startdate, biotext, supervisorid, birthDate)
VALUES
('dfe2f986-fac0-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Unreal','${aeskey}'),  PGP_SYM_ENCRYPT('Ulysses','${aeskey}'), PGP_SYM_ENCRYPT('Test Engineer 2','${aeskey}'), '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('St. Louis','${aeskey}'), PGP_SYM_ENCRYPT('testing2@objectcomputing.com','${aeskey}'), '010101012', '2021-05-22', PGP_SYM_ENCRYPT('Test user 2','${aeskey}'), '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '1950-01-01');

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



INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '43ee8e79-b33d-44cd-b23c-e183894ebfef');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '802cb1f5-a255-4236-8719-773fa53d79d9');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO member_roles
(roleid, memberid)
VALUES
('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '802cb1f5-a255-4236-8719-773fa53d79d9');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '1b4f99da-ef70-4a76-9b37-8bb783b749ad');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'b2d35288-7f1e-4549-aa2b-68396b162490');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '43ee8e79-b33d-44cd-b23c-e183894ebfef');

INSERT INTO member_roles
    (roleid, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO member_roles(
    roleid, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '066b186f-1425-45de-89f2-4ddcc6ebe237');

INSERT INTO member_roles(
    roleid, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '066b186f-1425-45de-89f2-4ddcc6ebe237');


INSERT INTO team
(id, name, description)
VALUES
('a8733740-cf4c-4c16-a8cf-4f928c409acc', PGP_SYM_ENCRYPT('Checkins Experts','${aeskey}'), PGP_SYM_ENCRYPT('Checkins Engineers of superior knowledge','${aeskey}'));

INSERT INTO team
(id, name, description)
VALUES
('e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', PGP_SYM_ENCRYPT('JavaScript Gurus','${aeskey}'), PGP_SYM_ENCRYPT('JavaScript Engineers of Outstanding Skill','${aeskey}'));

INSERT INTO team
(id, name, description)
VALUES
('036b95a5-357c-45bd-b60e-e8e2e1afec83', PGP_SYM_ENCRYPT('Micronaut Genii','${aeskey}'), PGP_SYM_ENCRYPT('Micronaut Engineers of Genius Caliber','${aeskey}'));

INSERT INTO team
(id, name, description)
VALUES
('e545dfa1-a07d-4099-9a5b-ed14f07b87cc', PGP_SYM_ENCRYPT('PMO Superness','${aeskey}'), PGP_SYM_ENCRYPT('Excellent PMO Artists','${aeskey}'));

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('d2ee49cb-9479-49fb-80d7-43c3c1b50f91', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '802cb1f5-a255-4236-8719-773fa53d79d9', true);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('0f299d11-df47-406f-a426-8e3160eaeb21', '036b95a5-357c-45bd-b60e-e8e2e1afec83', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('9e7e9577-a36b-4238-84cc-4f160ac60b40', 'e545dfa1-a07d-4099-9a5b-ed14f07b87cc', '2559a257-ae84-4076-9ed4-3820c427beeb', true);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('439ad8a8-500f-4f3f-963b-a86437d5820a', 'e545dfa1-a07d-4099-9a5b-ed14f07b87cc', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', false);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('8eea2f65-160c-4db7-9f6d-f367acd333fb', 'e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', '43ee8e79-b33d-44cd-b23c-e183894ebfef', false);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('c7b4d5e0-09ba-479a-8c40-ca9bbd8f217a', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '6884ab96-2275-4af9-89d8-ad84254d8759', false);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('20bf1ddb-53a0-436e-99dc-802c1199e282', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', false);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('f84a21ca-1579-4c6a-8148-6a355518797a', 'e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', true);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('122ca588-bf61-4aea-bb6e-39838328bf85', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', 'b2d35288-7f1e-4549-aa2b-68396b162490', true);

INSERT INTO team_member
(id, teamid, memberid, lead)
VALUES
('adff5631-d4dc-4c61-b3d4-232d1cce8ce0', 'a8733740-cf4c-4c16-a8cf-4f928c409acc', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('92e91c5a-cb00-461a-86b4-d01b3f07754e', '6884ab96-2275-4af9-89d8-ad84254d8759', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2020-09-29 17:40:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('1343411e-26bf-4274-81ca-1b46ba3f0cb0', '6884ab96-2275-4af9-89d8-ad84254d8759', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-09-29 10:40:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('8aa38f8c-2169-41b1-8548-1c2472fab7ff', 'b2d35288-7f1e-4549-aa2b-68396b162490', '6207b3fd-042d-49aa-9e28-dcc04f537c2d', '2020-09-29 15:40:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '6884ab96-2275-4af9-89d8-ad84254d8759', '2020-09-29 13:42:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '2020-09-29 11:32:29.04' , true);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('1f68cfdc-0a4b-4118-b38e-d862a8b82bbb', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-09-20 11:32:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('e60c3ca1-3894-4466-b418-9b743d058cc8', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-06-20 11:32:29.04' , false);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '6884ab96-2275-4af9-89d8-ad84254d8759', '2020-03-20 11:32:29.04' , true);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('10184287-1746-4827-93fe-4e13cc0d2a6d', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2021-02-25 11:32:29.04', true);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('bdea5de0-4358-4b33-9772-0cd953567540', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2021-03-05 11:32:29.04', true);

INSERT INTO checkins
(id, teammemberid, pdlid, checkindate, completed)
VALUES
('553aa528-d5f6-4d15-bfb6-b53738dc7954', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2022-01-16 11:32:29.04', true);

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description)
VALUES
('226a2ab8-03cc-4f9e-96c8-55cf187df045', '10184287-1746-4827-93fe-4e13cc0d2a6d', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s first note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description)
VALUES
('444f6923-7b8e-4d03-8d33-021e7a72653c', '10184287-1746-4827-93fe-4e13cc0d2a6d', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s first private note for Ulysses', '${aeskey}'));

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description)
VALUES
('c0d76e16-f96a-4598-8006-52b803e8b26d', 'bdea5de0-4358-4b33-9772-0cd953567540', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s second note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description)
VALUES
('cc47b557-ed78-45c4-b577-89c1c9e705bd', 'bdea5de0-4358-4b33-9772-0cd953567540', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', PGP_SYM_ENCRYPT('Geetika''s second private note for Ulysses', '${aeskey}'));

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description)
VALUES
('73a5e7b5-9292-45c0-a605-5b5c63230892', '553aa528-d5f6-4d15-bfb6-b53738dc7954', '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia''s first note for Ulysses', '${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description)
VALUES
('73a5e7b5-9292-45c0-a605-5b5c63230892', '553aa528-d5f6-4d15-bfb6-b53738dc7954', '59b790d2-fabc-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('Julia''s first private note for Ulysses', '${aeskey}'));

INSERT INTO checkin_notes
(id, checkinid, createdbyid, description)
VALUES
('e5449026-cd9a-4bed-a648-fe3ad9382831', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesses note','${aeskey}'));

INSERT INTO private_notes
(id, checkinid, createdbyid, description)
VALUES
('e5449026-cd9a-4bed-a648-fe3ad9382832', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Jesses private note','${aeskey}'));

INSERT INTO action_items
(id, checkinid, createdbyid, description)
Values('b0840fc5-9a8e-43d8-be99-9682fc32e69e', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Action Item for Holly Williams','${aeskey}'));

INSERT INTO action_items
(id, checkinid, createdbyid, description)
Values('9a779dec-c1b6-484e-ad76-38e7c06b011c', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', PGP_SYM_ENCRYPT('Another Action Item for Holly Williams','${aeskey}'));

INSERT INTO action_items
(id, checkinid, createdbyid, description)
Values('a6e2c822-feab-4c8b-b164-78158b2d4993', 'cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', PGP_SYM_ENCRYPT('Action Item for Jesse Hanner','${aeskey}'));

INSERT INTO action_items
(id, checkinid, createdbyid, description)
Values('0ead3434-82e7-47b4-a0ef-d1f44d01732b', '1343411e-26bf-4274-81ca-1b46ba3f0cb0', '6884ab96-2275-4af9-89d8-ad84254d8759', PGP_SYM_ENCRYPT('Action Item for Pramukh Bagur','${aeskey}'));

insert into guild (id, name, description)
values('ba42d181-3c5b-4ee3-938d-be122c314bee',  PGP_SYM_ENCRYPT('Software Engineering','${aeskey}'), PGP_SYM_ENCRYPT('Resource for Software Engineering Topics','${aeskey}'));

insert into guild (id, name, description)
values('06cd3202-a209-4ae1-a49a-10395fbe3548', PGP_SYM_ENCRYPT('Micronaut','${aeskey}'), PGP_SYM_ENCRYPT('For Micronaut Lovers and Learners','${aeskey}'));

insert into guild (id, name, description)
values('d1d4af0e-b1a5-47eb-be49-f3581271f1e3', PGP_SYM_ENCRYPT('Fullstack Development','${aeskey}'), PGP_SYM_ENCRYPT('Full Stack Development Interests','${aeskey}'));

insert into guild_member (id, guildId, memberId, lead)
values('fd976615-6a8b-4cd1-8aea-cb7751c8ee1a','ba42d181-3c5b-4ee3-938d-be122c314bee', 'b2d35288-7f1e-4549-aa2b-68396b162490', true);

insert into guild_member (id, guildId, memberId, lead)
values('86dc52b9-5b2a-4241-9c54-0fde07600c58','ba42d181-3c5b-4ee3-938d-be122c314bee', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', false);

insert into guild_member (id, guildId, memberId, lead)
values('7cd12bb9-6aa4-4edc-831f-f4ebe8f22f62','ba42d181-3c5b-4ee3-938d-be122c314bee', '6884ab96-2275-4af9-89d8-ad84254d8759', false);

insert into guild_member (id, guildId, memberId, lead)
values('8a20e99f-c326-4529-8024-26724a8586b1','ba42d181-3c5b-4ee3-938d-be122c314bee', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false);

insert into guild_member (id, guildId, memberId, lead)
values('7ffe3937-bdce-4ebb-a03d-8a8b7d4703ef','06cd3202-a209-4ae1-a49a-10395fbe3548', '802cb1f5-a255-4236-8719-773fa53d79d9', true);

insert into guild_member (id, guildId, memberId, lead)
values('dd694cf2-c0f9-4470-b897-00c564c1252b','06cd3202-a209-4ae1-a49a-10395fbe3548', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false);

INSERT INTO pulse_response
(id, submissiondate, updateddate, teammemberid, internalfeelings, externalfeelings)
VALUES
('cda41eed-70ea-4d3f-a9d7-cd0c5158eb5f', '2021-01-29', '2021-02-02', '8fa673c0-ca19-4271-b759-41cb9db2e83a',  PGP_SYM_ENCRYPT('Feeling pretty happy','${aeskey}'), PGP_SYM_ENCRYPT('Feeling really good','${aeskey}'));



insert into permissions
    (id, permission)
values
    ('439ad8a8-500f-4f3f-963b-a86437d5820a', 'CAN_CREATE_ORGANIZATION_MEMBERS');

insert into permissions
    (id, permission)
values
    ('0f299d11-df47-406f-a426-8e3160eaeb21', 'CAN_DELETE_ORGANIZATION_MEMBERS');

insert into permissions
    (id, permission)
values
    ('008f6641-0b0a-4e89-84f0-c580f912b80d', 'CAN_VIEW_FEEDBACK_REQUEST');

insert into permissions
    (id, permission)
values
    ('1bf32dfe-a204-4c80-889e-829ca66c999b', 'CAN_CREATE_FEEDBACK_REQUEST');

insert into permissions
    (id, permission)
values
    ('a574feb9-f2d4-4cbf-9353-bfaccdffa74f', 'CAN_DELETE_FEEDBACK_REQUEST');

insert into permissions
    (id, permission)
values
    ('26a2f861-3f7d-4dc3-8762-716b184a3a47', 'CAN_VIEW_FEEDBACK_ANSWER');

insert into permissions
    (id, permission)
values
    ('1fd790d9-df9a-4201-818b-3a9ac5e5be3b', 'CAN_VIEW_ROLE_PERMISSIONS');

insert into permissions
    (id, permission)
values
    ('f6961946-a792-4a16-b675-d8cf7980c17a', 'CAN_VIEW_PERMISSIONS');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '439ad8a8-500f-4f3f-963b-a86437d5820a');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '0f299d11-df47-406f-a426-8e3160eaeb21');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '008f6641-0b0a-4e89-84f0-c580f912b80d');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '1bf32dfe-a204-4c80-889e-829ca66c999b');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'a574feb9-f2d4-4cbf-9353-bfaccdffa74f');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '26a2f861-3f7d-4dc3-8762-716b184a3a47');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', '1fd790d9-df9a-4201-818b-3a9ac5e5be3b');

insert into role_permissions
    (roleid, permissionid)
values
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'f6961946-a792-4a16-b675-d8cf7980c17a');

insert into role_permissions
    (roleid, permissionid)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '008f6641-0b0a-4e89-84f0-c580f912b80d');

insert into role_permissions
    (roleid, permissionid)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', '1bf32dfe-a204-4c80-889e-829ca66c999b');

insert into role_permissions
    (roleid, permissionid)
values
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'a574feb9-f2d4-4cbf-9353-bfaccdffa74f');

insert into role_permissions
    (roleid, permissionid)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '439ad8a8-500f-4f3f-963b-a86437d5820a');

insert into role_permissions
    (roleid, permissionid)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '008f6641-0b0a-4e89-84f0-c580f912b80d');

insert into role_permissions
    (roleid, permissionid)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', '1bf32dfe-a204-4c80-889e-829ca66c999b');

insert into role_permissions
    (roleid, permissionid)
values
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'a574feb9-f2d4-4cbf-9353-bfaccdffa74f');

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
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

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
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

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
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

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
VALUES
('2cb80a06-e723-482f-af9b-6b9516cabfcd', 'Empty Template', 'This template does not have any questions on it', '2559a257-ae84-4076-9ed4-3820c427beeb', '2020-04-04', true, true, false);

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
VALUES
('492e4f61-c7e3-4c30-a650-7ec74f2ba545', 'Private Template', 'This template is private', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2020-06-07', true, false, false);

INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc)
VALUES
('c5d10880-f561-11eb-9a03-0242ac130003', 'Private Template 2', 'This template is private', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '2020-06-10', true, false, false);

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('d62b5c09-7ff9-4b0a-bfee-7f467470a7ef', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '18ef2032-c264-411e-a8e1-ddda9a714bae', '2020-07-07', '2021-08-01', null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('ab7b21d4-f88c-4494-9b0b-8541636025eb', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('2dd2347a-c296-4986-b428-3fbf6a24ea1e', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('c15961e4-6e9b-42cd-8140-ece9efe2445c', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498' , 'b2d35288-7f1e-4549-aa2b-68396b162490', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, '2020-07-08', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('e2e24336-0615-4564-af29-d0f7b3ac3db9', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '8fa673c0-ca19-4271-b759-41cb9db2e83a', '6884ab96-2275-4af9-89d8-ad84254d8759', '18ef2032-c264-411e-a8e1-ddda9a714bae', '2018-12-24', '2018-12-25', null, 'sent');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('09fbdaf2-f554-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9','97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-07', null, '2020-07-07', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('82d9db7c-f554-11eb-9a03-0242ac130003', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'b2d35288-7f1e-4549-aa2b-68396b162490', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9','97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2020-07-05', null, '2020-07-10', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('e2af1c96-a593-48c2-b9e0-a00193a070c7', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', '8fa673c0-ca19-4271-b759-41cb9db2e83a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-08-01', '2021-08-05', '2021-08-02', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('d09031be-fac1-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '43ee8e79-b33d-44cd-b23c-e183894ebfef','18ef2032-c264-411e-a8e1-ddda9a714bae', '2020-08-01', '2020-08-05', '2020-08-02', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('a50f2f8a-7eb0-4456-b5ef-382086827ba0', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '2559a257-ae84-4076-9ed4-3820c427beeb', '1c8bc142-c447-4889-986e-42ab177da683', '2022-04-14', null, null, 'sent');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('dbd2da2b-df0a-4e11-9fcd-ed0774a5fdea', PGP_SYM_ENCRYPT('They have strong engineering and verbal skills, but could be better at being tactful with client requests..','${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f', 'd09031be-fac1-11eb-9a03-0242ac130003', 0.5);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('766a3a2c-88de-4487-b285-e3c667ffe0e6', PGP_SYM_ENCRYPT('While they do a good job of innovating courageously, like I said, their presence sometimes impacts client relations.','${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', 'd09031be-fac1-11eb-9a03-0242ac130003', -0.2);

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('b5596a80-fac3-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '1b4f99da-ef70-4a76-9b37-8bb783b749ad','18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-02-15', '2020-02-25', '2020-02-20', 'submitted');

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('94550858-facd-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('They have done a great job on this project, but could have spoken to the client a litle more about their options.', '${aeskey}'), 'd6d05f53-682c-4c37-be32-8aab5f89767f', 'b5596a80-fac3-11eb-9a03-0242ac130003', 0.7);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('98e0c286-facd-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('I have few complaints except the aforementioned need to communicate more with the client', '${aeskey}'), '47f997ca-0045-4147-afcb-0c9ed0b44978', 'b5596a80-fac3-11eb-9a03-0242ac130003', 0.2);

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('ab2da7fc-fac2-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', '2dee821c-de32-4d9c-9ecb-f73e5903d17a', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3' ,'18ef2032-c264-411e-a8e1-ddda9a714bae', '2021-08-01', '2021-08-05', null, 'pending');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
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

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('e238dd00-fac4-11eb-9a03-0242ac130003', '59b790d2-fabc-11eb-9a03-0242ac130003', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '43ee8e79-b33d-44cd-b23c-e183894ebfef', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2021-03-22', '2021-04-01', '2021-04-01', 'submitted');

INSERT INTO feedback_requests
(id, creator_id, requestee_id, recipient_id, template_id, send_date, due_date, submit_date, status)
VALUES
('4240735d-15fd-4eea-8bca-8c642a433036', '59b790d2-fabc-11eb-9a03-0242ac130003', 'dfe2f986-fac0-11eb-9a03-0242ac130003', '066b186f-1425-45de-89f2-4ddcc6ebe237', '97b0a312-e5dd-46f4-a600-d8be2ad925bb', '2021-03-22', '2021-04-01', '2021-04-01', 'submitted');

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
('d19e6ac1-f081-414c-a51a-ccc684131bec', PGP_SYM_ENCRYPT('They could definitely learn how to use Vim better. They keep asking me how to exit!', '${aeskey}'), '89c8b612-fca8-4144-88cd-176ddfca35ad', '4240735d-15fd-4eea-8bca-8c642a433036', 0.3);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('5a65fe6b-0f27-4d2c-bc25-a637bc33d630', PGP_SYM_ENCRYPT('Nothing comes to mind.', '${aeskey}'), '3571cf89-22b9-4e0e-baff-1a1e45482472', 'e238dd00-fac4-11eb-9a03-0242ac130003', 0.5);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('8c13ffa2-fad0-11eb-9a03-0242ac130003', PGP_SYM_ENCRYPT('They are very good at working on a team--all of us is better than any one of us', '${aeskey}'), 'afa7e2cb-366a-4c16-a205-c0d493b80d85', 'e238dd00-fac4-11eb-9a03-0242ac130003', 0.8);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('603b1308-6cc4-4534-b588-921b7b3e476d', PGP_SYM_ENCRYPT('There was one time where I could not figure out the cause of a bug. They showed me how to use the debugging software to locate the bug, which was extremely helpful. I was able to quickly patch the bug after that.', '${aeskey}'), '3571cf89-22b9-4e0e-baff-1a1e45482472', '4240735d-15fd-4eea-8bca-8c642a433036', 0.9);

INSERT INTO feedback_answers
(id, answer, question_id, request_id, sentiment)
VALUES
('a223135a-742b-45c6-b9a4-2bb990d956b2', PGP_SYM_ENCRYPT('They are always punctual, and work well with the other members of the team. Although they have a few technical skills they could brush up on, our team is lucky to have them.', '${aeskey}'), 'afa7e2cb-366a-4c16-a205-c0d493b80d85', '4240735d-15fd-4eea-8bca-8c642a433036', 0.7);