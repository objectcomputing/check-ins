ALTER TABLE member_skills ADD COLUMN interested boolean DEFAULT false;

-- If the original skilllevel was 'interested', set the new column value to true
UPDATE member_skills SET interested = true WHERE skilllevel = '1';

-- Transition old skill levels to the new range
UPDATE member_skills SET skilllevel = '0' WHERE skilllevel = '1';
UPDATE member_skills SET skilllevel = '1' WHERE skilllevel = '2';
UPDATE member_skills SET skilllevel = '2' WHERE skilllevel = '3' OR
                                                skilllevel = '4';
UPDATE member_skills SET skilllevel = '3' WHERE skilllevel = '5';
