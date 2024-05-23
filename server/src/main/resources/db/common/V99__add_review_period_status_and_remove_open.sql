ALTER TABLE review_periods ADD COLUMN review_status varchar;

UPDATE review_periods
SET review_status = CASE
                 WHEN open = true THEN 'OPENED'
                 WHEN open = false THEN 'CLOSED'
                 ELSE 'UNKNOWN' END;

ALTER TABLE review_periods DROP COLUMN open;