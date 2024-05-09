ALTER TABLE pulse_response DROP COLUMN updatedDate;

ALTER TABLE pulse_response ADD COLUMN internal_score Integer;
ALTER TABLE pulse_response ADD COLUMN external_score Integer;