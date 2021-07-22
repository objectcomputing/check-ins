ALTER TABLE frozen_templates
DROP template_creator_id;

ALTER TABLE frozen_templates
ADD COLUMN original_template_id varchar references feedback_templates(id);