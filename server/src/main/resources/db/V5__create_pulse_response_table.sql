drop table if exists pulse_response;
CREATE TABLE pulse_response (
   id varchar PRIMARY KEY,
   submissionDate date,
   updatedDate date,
   teamMemberId varchar REFERENCES member_profile(uuid),
   internalFeelings varchar,
   externalFeelings varchar
);