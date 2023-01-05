ALTER TABLE onboard_profile
ADD COLUMN backgroundid varchar UNIQUE references background_information(id);