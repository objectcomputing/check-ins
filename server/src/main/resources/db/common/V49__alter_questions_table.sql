ALTER TABLE questions
ADD column categoryId varchar REFERENCES question_categories(id);