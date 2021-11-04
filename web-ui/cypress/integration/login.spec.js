
describe("Tests that login page exists and functions properly", () => {

  it("ensure components are displayed on home page before login", () => {
    cy.visit("/oauth/login/google");
    cy.get("form");
    cy.get('input[name="email"]')                     // Ensure input for email exists
      .type("sharmag@objectcomputing.com")
      .should("have.value", "sharmag@objectcomputing.com");
    cy.get('input[name="role"]')                      // Ensure input for role exists
      .type("ADMIN")
      .should("have.value", "ADMIN")
    cy.get('input[value="Submit"]').click()           // Ensure submit button exists and take you to checkins page.
    cy.url().should("include", "/")
    cy.request("/").then((response) => {
      expect(response.status).to.eq(200)
    });
  });

});