ALTER TABLE questions
ADD column category varchar REFERENCES question_categories(id);