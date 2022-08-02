CREATE TABLE onboardee_employment_eligibility(
        id varchar PRIMARY KEY,
        ageLegal boolean,
        usCitizen boolean,
        visaStatus varchar,
        expirationDate varchar,
        felonyStatus boolean,
        felonyExplanation varchar
);
