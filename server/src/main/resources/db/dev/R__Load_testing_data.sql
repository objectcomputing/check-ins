delete from action_items;
delete from agenda_items;
delete from checkin_document;
delete from checkin_notes;
delete from private_notes;
delete from checkins;
delete from guildmembers;
delete from guilds;
delete from member_skills;
delete from pulse_response;
delete from questions;
delete from role;
delete from team_member;
delete from team;
delete from member_profile;

INSERT INTO member_profile
    (id, name, title, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('01b7d769-9fa2-43ff-95c7-f3b950a27bf9', 'Gina Bremehr', 'COO', 'St. Louis', 'bremehrg@objectcomputing.com', '1231234', '2012-09-29', 'Epitome of Strong Woman', null);

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('2559a257-ae84-4076-9ed4-3820c427beeb', 'Ron Steinkamp', 'Senior Project Manager', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', 'St. Louis', 'steinkampr@objectcomputing.com', '3333333', '2019-09-19', 'Managing projets well', null);

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('802cb1f5-a255-4236-8719-773fa53d79d9', 'John Meyerin', 'Software Engineer', '2559a257-ae84-4076-9ed4-3820c427beeb', 'St. Louis', 'meyerinj@objectcomputing.com', '1111111', '2018-09-03', 'Outstanding Engineer', null);

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'Geetika Sharma', 'PMO Administrator', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9', 'St. Louis', 'sharmag@objectcomputing.com', '4444444', '2019-09-09', 'Engineer Wrangler extrodinaire', null);

INSERT INTO member_profile
(id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
('8fa673c0-ca19-4271-b759-41cb9db2e83a', 'Holly Williams', 'Software Engineer', '802cb1f5-a255-4236-8719-773fa53d79d9', 'St. Louis', 'williamsh@objectcomputing.com', '8888888', '2018-11-13', 'Software Engineer Remarkable', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('6207b3fd-042d-49aa-9e28-dcc04f537c2d', 'Michael Kimberlin', 'Director of Organizational Development', '8fa673c0-ca19-4271-b759-41cb9db2e83a', 'St. Louis', 'kimberlinm@objectcomputing.com', '2222222', '2007-01-11', 'Developer of developers', null);

INSERT INTO member_profile
    (id, name, title, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', 'Mark Volkmann', 'Partner and Principal Software Engineer', 'St. Louis', 'volkmannm@objectcomputing.com', '9999999', '1996-06-13', 'Software Engineer Spectacular', null);

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', 'Jesse Hanner', 'Software Engineer', '802cb1f5-a255-4236-8719-773fa53d79d9', 'St. Louis', 'hannerj@objectcomputing.com', '1234567', '2018-01-10', 'Amazing Engineer', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('6884ab96-2275-4af9-89d8-ad84254d8759', 'Pramukh Bagur', 'Software Engineer', '802cb1f5-a255-4236-8719-773fa53d79d9', 'St. Louis', 'bagurp@objectcomputing.com', '9876543', '2018-09-09', 'Top notch Engineer', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('1b4f99da-ef70-4a76-9b37-8bb783b749ad', 'Suman Maroju', 'Software Engineer', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'St. Louis', 'marojus@objectcomputing.com', '2345678', '2018-06-06', 'Superior Engineer', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('b2d35288-7f1e-4549-aa2b-68396b162490', 'Mohit Bhatia', 'Senior Software Engineer', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498', 'St. Louis', 'bhatiam@objectcomputing.com', '5555555', '2013-11-13', 'Engineer Extrodinaire', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_profile
    (id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
    ('43ee8e79-b33d-44cd-b23c-e183894ebfef', 'Zack Brown', 'Intern', '2559a257-ae84-4076-9ed4-3820c427beeb', 'St. Louis', 'brownz@objectcomputing.com', '5555555', '2020-06-06', 'Intern Extrodinaire', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO member_profile
(id, name, title, pdlid, location, workEmail, insperityid, startdate, biotext, supervisorid)
VALUES
('066b186f-1425-45de-89f2-4ddcc6ebe237', 'Joe Warner', 'Software Engineer', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d', 'St. Louis', 'warnerj@objectcomputing.com', '1234569', '2019-03-25', 'Engineer of Some Note', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('e8a4fff8-e984-4e59-be84-a713c9fa8d23', 'ADMIN', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('8bda2ae9-58c1-4843-a0d5-d0952621f9df', 'ADMIN', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('d03f5f0b-e29c-4cf4-9ea4-6baa09405c56', 'ADMIN', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('b72789ec-18a0-11eb-adc1-0242ac120002', 'ADMIN', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('9219ca40-6c74-419e-ab8a-22cc2f4e90dd', 'ADMIN', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('1cf0b520-925e-43b1-8cd3-c06559b402b7', 'PDL', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('26c67a0c-12d3-48d2-bc4a-d451ff9f7b3e', 'PDL', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('b550aec4-d3b9-4560-aa08-25f2f2884ca1', 'PDL', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('517d324c-3d25-4ffe-887e-0de3ddc8fff9', 'PDL', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('503fd79e-3e4d-4dc2-8045-498d329fd51f', 'PDL', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('58f21738-43a9-4e3d-ac6d-8d9acb9b1fb9', 'PDL', '802cb1f5-a255-4236-8719-773fa53d79d9');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('388cb81a-488d-4c66-b22c-368deb20f112', 'PDL', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('883250f1-e26e-490e-bcec-be19d1e0538c', 'MEMBER', '01b7d769-9fa2-43ff-95c7-f3b950a27bf9');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('f7222c46-5200-4db7-a2ad-f991bb1a3137', 'MEMBER', '2559a257-ae84-4076-9ed4-3820c427beeb');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('906ef6cc-261e-4920-b305-d35926325854', 'MEMBER', '802cb1f5-a255-4236-8719-773fa53d79d9');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('fae1416d-0f21-4e7b-8fa5-5097573f2eb6', 'MEMBER', '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('859f20cc-cf45-4559-9ea8-b6038c098fac', 'MEMBER', '6207b3fd-042d-49aa-9e28-dcc04f537c2d');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('14af8d18-0932-4a8c-aa5a-0fb794ade388', 'MEMBER', '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('111c71c9-ecd9-4e8b-ab27-e2049d40de58', 'MEMBER', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('d18ff24d-fa2f-4eaf-adf8-99617bbf782c', 'MEMBER', '6884ab96-2275-4af9-89d8-ad84254d8759');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('7115d961-031d-42de-8918-1ab5c1806bcb', 'MEMBER', '1b4f99da-ef70-4a76-9b37-8bb783b749ad');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('4b2fb1e4-7761-45d4-b727-8d8f11e97f95', 'MEMBER', 'b2d35288-7f1e-4549-aa2b-68396b162490');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('cfc5b0f8-e072-4160-91a5-1dddb085fb8b', 'MEMBER', '43ee8e79-b33d-44cd-b23c-e183894ebfef');

INSERT INTO role
    (id, role, memberid)
VALUES
    ('58199639-e670-4702-bba0-8fc63457cd02', 'MEMBER', '8fa673c0-ca19-4271-b759-41cb9db2e83a');

INSERT INTO role(
    id, role, memberid)
VALUES
    ('a0ac5d7a-4b8e-11eb-b393-b35b67f02aa6', 'ADMIN', '066b186f-1425-45de-89f2-4ddcc6ebe237');

INSERT INTO team
    (id, name, description)
VALUES
    ('a8733740-cf4c-4c16-a8cf-4f928c409acc', 'Checkins Experts', 'Checkins Engineers of superior knowledge');

INSERT INTO team
    (id, name, description)
VALUES
    ('e8f052a8-40b5-4fb4-9bab-8b16ed36adc7', 'JavaScript Gurus', 'JavaScript Engineers of Outstanding Skill');

INSERT INTO team
    (id, name, description)
VALUES
    ('036b95a5-357c-45bd-b60e-e8e2e1afec83', 'Micronaut Genii', 'Micronaut Engineers of Genius Caliber');

INSERT INTO team
    (id, name, description)
VALUES
    ('e545dfa1-a07d-4099-9a5b-ed14f07b87cc', 'PMO Superness', 'Excellent PMO Artists');

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
    ('cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-09-29 13:42:29.04' , false);

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
    ('ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', '802cb1f5-a255-4236-8719-773fa53d79d9', '2020-03-20 11:32:29.04' , true);

INSERT INTO checkin_notes
    (id, checkinid, createdbyid, description)
VALUES
    ('e5449026-cd9a-4bed-a648-fe3ad9382831', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', 'Jesses note');

INSERT INTO private_notes
    (id, checkinid, createdbyid, description)
VALUES
    ('e5449026-cd9a-4bed-a648-fe3ad9382832', 'ff52e697-55a1-4a89-a13f-f3d6fb8f6b3d', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', 'Jesses private note');

INSERT INTO action_items
    (id, checkinid, createdbyid, description)
Values('b0840fc5-9a8e-43d8-be99-9682fc32e69e', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', 'Action Item for Holly Williams');

INSERT INTO action_items
    (id, checkinid, createdbyid, description)
Values('9a779dec-c1b6-484e-ad76-38e7c06b011c', '92e91c5a-cb00-461a-86b4-d01b3f07754e', '8fa673c0-ca19-4271-b759-41cb9db2e83a', 'Another Action Item for Holly Williams');

INSERT INTO action_items
    (id, checkinid, createdbyid, description)
Values('a6e2c822-feab-4c8b-b164-78158b2d4993', 'cf806bb5-7269-48ee-8b72-0b2762c7669f', '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3', 'Action Item for Jesse Hanner');

INSERT INTO action_items
    (id, checkinid, createdbyid, description)
Values('0ead3434-82e7-47b4-a0ef-d1f44d01732b', '1343411e-26bf-4274-81ca-1b46ba3f0cb0', '6884ab96-2275-4af9-89d8-ad84254d8759', 'Action Item for Pramukh Bagur');

insert into guilds (id, name, description)
values('ba42d181-3c5b-4ee3-938d-be122c314bee', 'Software Engineering', 'Resource for Software Engineering Topics');

insert into guilds (id, name, description)
values('06cd3202-a209-4ae1-a49a-10395fbe3548', 'Micronaut', 'For Micronaut Lovers and Learners');

insert into guilds (id, name, description)
values('d1d4af0e-b1a5-47eb-be49-f3581271f1e3', 'Fullstack Development', 'Full Stack Development Interests');
insert into guildmembers (id, guildid, memberid, lead)
values('fd976615-6a8b-4cd1-8aea-cb7751c8ee1a','ba42d181-3c5b-4ee3-938d-be122c314bee', 'b2d35288-7f1e-4549-aa2b-68396b162490', true);

insert into guildmembers (id, guildid, memberid, lead)
values('86dc52b9-5b2a-4241-9c54-0fde07600c58','ba42d181-3c5b-4ee3-938d-be122c314bee', '1b4f99da-ef70-4a76-9b37-8bb783b749ad', false);

insert into guildmembers (id, guildid, memberid, lead)
values('7cd12bb9-6aa4-4edc-831f-f4ebe8f22f62','ba42d181-3c5b-4ee3-938d-be122c314bee', '6884ab96-2275-4af9-89d8-ad84254d8759', false);

insert into guildmembers (id, guildid, memberid, lead)
values('8a20e99f-c326-4529-8024-26724a8586b1','ba42d181-3c5b-4ee3-938d-be122c314bee', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false);

insert into guildmembers (id, guildid, memberid, lead)
values('7ffe3937-bdce-4ebb-a03d-8a8b7d4703ef','06cd3202-a209-4ae1-a49a-10395fbe3548', '802cb1f5-a255-4236-8719-773fa53d79d9', true);

insert into guildmembers (id, guildid, memberid, lead)
values('dd694cf2-c0f9-4470-b897-00c564c1252b','06cd3202-a209-4ae1-a49a-10395fbe3548', '8fa673c0-ca19-4271-b759-41cb9db2e83a', false);
