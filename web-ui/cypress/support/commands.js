// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

const authUrl = "http://localhost:8080/oauth/login/google";

Cypress.Commands.add("loginByCsrf", (email, role) => {
  cy.request(authUrl)
    .its("headers")
    .then((headers) => {
      const csrf = headers["x-csrf-token"];
      cy.request({
        method: "POST",
        url: authUrl,
        failOnStatusCode: false,
        form: true,
        body: {
          email,
          role,
          _csrf: csrf
        }
      });
    });
});
