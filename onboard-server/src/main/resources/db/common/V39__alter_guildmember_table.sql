ALTER TABLE guildmembers
DROP CONSTRAINT guildmembers_guildid_fkey;

ALTER TABLE guildmembers
ADD CONSTRAINT guildmembers_guildid_fkey FOREIGN KEY (guildid) REFERENCES guilds(id) ON DELETE CASCADE;