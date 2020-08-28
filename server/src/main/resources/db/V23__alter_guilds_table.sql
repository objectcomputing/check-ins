ALTER TABLE guilds
RENAME COLUMN guildid TO id; 

ALTER TABLE guildMembers
ADD FOREIGN KEY (guildid) REFERENCES guilds(id); 