delete from feedback_answers where request_id in (select id from feedback_requests where template_id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea'));
delete from feedback_requests where template_id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea');
delete from review_periods where review_template_id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea');
delete from review_periods where self_review_template_id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea');
delete from template_questions where template_id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea');
delete from feedback_templates where id in ('b4d408e8-182e-4aab-830e-57596edf9a7f', 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea');

---- Self Review - 2024
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc, is_review) -- created by: Mischievous Kangaroo
VALUES
('b4d408e8-182e-4aab-830e-57596edf9a7f', 'Self Review - 2024', 'This survey is intended for performance self-review.', 'e4b2fe52-1915-4544-83c5-21b8f871f6db', '2022-11-01', true, true, false, true);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('aa9e97b8-1b25-4ee1-a337-3e821802a969', PGP_SYM_ENCRYPT('Individual Performance and Contributions', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 1, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('3451bb90-8b61-46f1-b991-fb98b6871f94', PGP_SYM_ENCRYPT('My performance (contributions, knowledge, & skill) was strong during the period covered by this review.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 2, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('6330a931-840f-4402-8a4b-78f17af731d2', PGP_SYM_ENCRYPT('During the period covered by this review, I demonstrated a commitment to ensuring an exceptional client experience wherever possible.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 3, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('ec93ef57-bec6-40b4-98c5-7ba72639294a', PGP_SYM_ENCRYPT('Can you please share any qualities, behaviors, or examples that demonstrate your effectiveness as an individual contributor during this period?', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 4, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('f1f00c55-4a57-42fe-8024-2e87cec1e877', PGP_SYM_ENCRYPT('Can you please share, if applicable, anything that you feel may be holding you back from being a more effective individual contributor?', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 5, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('991f4b96-a9ed-4a5a-b405-3d0ebc69bba7', PGP_SYM_ENCRYPT('Team Collaboration and Leadership', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 6, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('e3248688-fd26-4c8c-9110-af0b760c2e55', PGP_SYM_ENCRYPT('I openly welcome collaboration and input where appropriate.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 7, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('e951a8db-7f87-4d35-8419-d06dc7687eba', PGP_SYM_ENCRYPT('I frequently seek to share with and uplift other members of our OCI team.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 8, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('88140f0d-659f-4340-a9b3-4c6130327ed1', PGP_SYM_ENCRYPT('I have led or helped supported the personal or professional growth of other members of our OCI team during the period covered by this review.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 9, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('e080a8ff-7bce-4a5b-bf29-645266548e73', PGP_SYM_ENCRYPT('During the period covered by this review, I demonstrated a commitment to ensuring an exceptional experience for our fellow team members wherever possible.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 10, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('fdad94b8-f91c-4cd5-9265-526a50a4d9c7', PGP_SYM_ENCRYPT('Can you please share any qualities, behaviors, or examples that demonstrate your effectiveness as a collaborator or leader during this period?', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 11, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('002bac4a-ad06-4fd1-a565-7cb300a7532c', PGP_SYM_ENCRYPT('Can you please share, if applicable, anything that you feel may be holding you back from being a more effective collaborator or leader?', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 12, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('c4f0278c-7a53-4b40-8819-72f6d330aaf3', PGP_SYM_ENCRYPT('Commitment to Our Business', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 13, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('d539e697-c957-4afe-a07f-19a1dd4295e5', PGP_SYM_ENCRYPT('Please share how you have demonstrated a commitment to our business during the period covered by this review (e.g., impacting our reputation and goodwill in the community, impacting revenue growth, etc.).', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 14, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('7820e3ed-9157-4cc2-9691-750c4c7f5a31', PGP_SYM_ENCRYPT('Citizenship and Values', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 15, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('5d48c2aa-3c0a-43fb-95e3-e717b368ff89', PGP_SYM_ENCRYPT('Please share how you have demonstrated a commitment to our values during the period covered by this review (i.e. all of us is better than any one of us, innovate courageously and mindfully, share it back).', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 16, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('675e63dc-b5c8-47c2-bf28-6fb627dedefe', PGP_SYM_ENCRYPT('Please provide any additional information or context that you feel would be helpful in assessing your performance for the period covered by this review.', '${aeskey}'), 'b4d408e8-182e-4aab-830e-57596edf9a7f', 17, 'TEXT');

---- Annual Review - 2024
INSERT INTO feedback_templates
(id, title, description, creator_id, date_created, active, is_public, is_ad_hoc, is_review) -- created by: Big Boss
VALUES
('e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 'Annual Review - 2024', 'This survey is intended for performance review.', 'e4b2fe52-1915-4544-83c5-21b8f871f6db', '2022-11-21', true, true, false, true);

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('e2c68178-9b24-4dba-bd2b-55ca4c82c8b2', PGP_SYM_ENCRYPT('Individual Performance and Contributions', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 1, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('3c62cc59-49ea-4ac6-b53f-ac784a7f7067', PGP_SYM_ENCRYPT('This team member''s performance (contributions, knowledge, & skill) was strong during the period covered by this review.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 2, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('ddb5ea1b-6146-4e52-9c25-ec4c9b8a92af', PGP_SYM_ENCRYPT('This team member''s potential to contribute is high.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 3, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('8ecce381-2e39-4f33-8ead-d62d364b5891', PGP_SYM_ENCRYPT('During the period covered by this review, this team member demonstrated a commitment to ensuring an exceptional client experience wherever possible.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 4, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('59e8c428-8970-4ccb-a5af-2dd9d5f5a4e1', PGP_SYM_ENCRYPT('Can you please share any qualities, behaviors, or examples that demonstrate this team member''s effectiveness as an individual contributor during this period?', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 5, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('dddd120d-5c7c-4689-9f19-38980d650de1', PGP_SYM_ENCRYPT('Can you please share any qualities or behaviors, if applicable, that may be holding this person back from being a more effective individual contributor?', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 6, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('3d4f3ff6-517d-48bd-986e-e4c156e84e51', PGP_SYM_ENCRYPT('Team Collaboration and Leadership', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 7, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('518ba915-a44c-4b0d-815f-ae8abc5e3b24', PGP_SYM_ENCRYPT('This team member welcomes collaboration and input where appropriate.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 8, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('475a8e17-3cab-45c3-8da5-586ee6bd212e', PGP_SYM_ENCRYPT('This team member frequently seeks to share with and uplift other members of our OCI team.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 9, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('8e424109-0624-47aa-a9b5-907ed02c5b03', PGP_SYM_ENCRYPT('This team member has led or helped support the personal or professional growth of other members of our OCI team during the period covered by this review.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 10, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('bc0ad36b-b7ed-4c67-8d0b-94d9907d31f8', PGP_SYM_ENCRYPT('During the period covered by this review, this team member demonstrated a commitment to ensuring an exceptional experience for our fellow team members wherever possible.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 11, 'SLIDER');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('1bc42e13-df19-42dd-8734-46b42e461425', PGP_SYM_ENCRYPT('Can you please share any qualities, behaviors, or examples that demonstrate this team member''s effectiveness as a collaborator or leader during this period?', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 12, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('d3bc80fc-82e4-4793-b2c1-116d7b967b3d', PGP_SYM_ENCRYPT('Can you please share any qualities or behaviors, if applicable, that may be holding this person back from being a more effective collaborator or leader?', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 13, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('bf18b24a-ff31-4def-a07d-7f60a6985bc5', PGP_SYM_ENCRYPT('Commitment to Our Business', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 14, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('98b34fdb-7ac5-4902-9d5b-d91bb35c591f', PGP_SYM_ENCRYPT('Please share how this team member has demonstrated a commitment to our business during the period covered by this review (e.g., impacting our reputation and goodwill in the community, impacting revenue growth, etc.).', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 15, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('2c5e8ecf-5531-4356-8688-dd6c481552f5', PGP_SYM_ENCRYPT('Citizenship and Values', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 16, 'NONE');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('af5bcbec-8367-4a24-b579-b18151fe435c', PGP_SYM_ENCRYPT('Please share how this team member has demonstrated a commitment to our values during the period covered by this review (i.e. all of us is better than any one of us, innovate courageously and mindfully, share it back).', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 17, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('d41656e0-a5a9-4d28-a107-31ea171ad044', PGP_SYM_ENCRYPT('Please provide any additional information or context that you feel would be helpful in assessing this team member''s performance for the period covered by this review.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 18, 'TEXT');

INSERT INTO template_questions
(id, question, template_id, question_number, input_type)
VALUES
('4e3bed99-c3fb-4445-a9cf-89098119ef15', PGP_SYM_ENCRYPT('This team member should be considered for a promotion.', '${aeskey}'), 'e5d22b5d-c23e-40dc-9e95-057b9876a0ea', 19, 'RADIO');
