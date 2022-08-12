CREATE TABLE employment_desired_availability(
        id varchar PRIMARY KEY,
        desiredposition varchar,
        desiredstartdate varchar,
        desiredsalary varchar,
        currentlyemployed BOOLEAN,
        contactcurrentemployer BOOLEAN,
        previousemploymentoci BOOLEAN,
        noncompeteagreement BOOLEAN,
        noncompeteexpirationdate varchar
);
