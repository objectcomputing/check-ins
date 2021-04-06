

--Insert Member roles for everyone that isn't Michael or Aaron.
INSERT INTO role ( memberid, role, id )
SELECT member_profile.id, 'MEMBER', uuid_in(overlay(overlay(md5(random()::text || ':' || clock_timestamp()::text) placing '4' from 13) placing to_hex(floor(random()*(11-8+1) + 8)::int)::text from 17)::cstring)
from member_profile
where member_profile.id NOT IN ('e4b2fe52-1915-4544-83c5-21b8f871f6db', 'c40e7f40-4cbd-4989-9055-b7a1524a3e6a')

--Insert PDL roles for everyone that isn't Michael or Aaron.
INSERT INTO role ( memberid, role, id )
SELECT member_profile.id, 'PDL', uuid_in(overlay(overlay(md5(random()::text || ':' || clock_timestamp()::text) placing '4' from 13) placing to_hex(floor(random()*(11-8+1) + 8)::int)::text from 17)::cstring)
from member_profile
where member_profile.id NOT IN ('e4b2fe52-1915-4544-83c5-21b8f871f6db', 'c40e7f40-4cbd-4989-9055-b7a1524a3e6a')


SELECT id, PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as firstName, PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as middleName, PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as lastName, PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as suffix, PGP_SYM_DECRYPT(cast(title as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as title, pdlid, PGP_SYM_DECRYPT(cast(location as bytea), '4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as location, PGP_SYM_DECRYPT(cast(workEmail as bytea), '4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as workEmail, employeeId, startDate, PGP_SYM_DECRYPT(cast(bioText as bytea), '4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') as bioText, supervisorid, terminationDate FROM member_profile mp WHERE (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.firstName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.middleName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.lastName as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.suffix as bytea),'4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.title as bytea), '4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR mp.pdlId = NULL) AND (NULL IS NULL OR PGP_SYM_DECRYPT(cast(mp.workEmail as bytea), '4A3B158163E9CDC724789EF5F0AEDFF3B76AFBDA715D3F51') = NULL) AND (NULL IS NULL OR mp.supervisorId = NULL) AND (((FALSE IS FALSE OR FALSE IS NULL) AND (mp.terminationdate IS NULL OR mp.terminationdate >= CURRENT_DATE)) OR (FALSE IS TRUE AND mp.terminationdate < CURRENT_DATE))