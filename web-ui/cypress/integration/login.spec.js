
describe("Tests for login page", () => {

  // TODO: setup http://localhost:8080 as base url in configuration
  it("ensure components are displayed", () => {
    cy.visit("https://checkins.objectcomputing.com");
    cy.get("form");
    cy.get('input[name="email"]')                     // Ensure input for email exists
      .type("sharmag@objectcomputing.com")
      .should("have.value", "sharmag@objectcomputing.com");
    cy.get('input[name="role"]')                      // Ensure input for role exists
      .type("ADMIN")
      .should("have.value", "ADMIN")
    cy.get('input[name="Submit"]')                    // Ensure submit button exists
      .type("Submit")
  });

  // it("ensure rest call was initiated on form submit", () => {
  //   cy.visit("http://localhost:8080/oauth/login/google");
  //   cy.get("form");
  //   cy.get('input[name="email"]').type("sharmag@objectcomputing.com");
  //   cy.get('input[name="role"]').type("ADMIN");
  //   cy.get('input[name="Submit"]').click();
  //   // assert that a post call was intercepted
  // });


















  // before( () => {
  //   const email = "sharmag@objectcomputing.com";
  //   const role = "SUPER";
  //   cy.loginByCsrf(email, role).then((res) => {
  //     expect(res.status).to.eq(200);
  //   });
  // });
  //
  // it("assert true", () => {
  //   expect(true).to.equal(true);
  // });
  //
  // it("navigates to the homepage", () => {
  //   cy.visit("localhost:8080");
  // });

  // it("navigates to the homepage", () => {
  //   cy.visit("localhost:8080");
  //   cy.get("a[href*='/checkins']").last().click();
  //   cy.url().should("include", "/checkins");
  //
  //   cy.contains("Request Feedback").first().click();
  //   cy.url().should("include", "/feedback/request");
  //   cy.url().should("include", "for=");
  //   cy.contains("Feedback Request for");
  //
  //   cy.contains("Survey 1").click();
  //   cy.url().should("include", "template=");
  //   cy.contains("Next").click();
  //
  //   cy.url().should("include", "step=2");
  //   cy.get(".member-card").click({multiple: true});
  //   cy.url().should("match", /from=*&*/);
  //   cy.contains("Next").click();
  //
  //   cy.url().should("include", "step=3");
  //
  //   cy.contains("Send Date:").click();
  //   cy.contains("OK").click();
  //   cy.url().should("include", "send=");
  //   cy.contains("Due Date:").click();
  //   cy.contains("OK").click();
  //   cy.url().should("include", "due=");
  //
  //   cy.contains("Submit").click();
  //   cy.contains("Feedback scheduled for today");
  //   cy.url().should("include", "/feedback/request/confirmation");
  // });
});