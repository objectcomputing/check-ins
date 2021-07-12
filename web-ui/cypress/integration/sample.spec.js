
describe("make a feedback request", () => {

  before( () => {
    const email = "sharmag@objectcomputing.com";
    const role = "SUPER";
    cy.loginByCsrf(email, role).then((res) => {
      expect(res.status).to.eq(200);
    });
  });

  it("navigates to the homepage", () => {
    cy.visit("localhost:3000");
    cy.get("a[href*='/checkins']").last().click();
    cy.url().should("include", "/checkins");

    cy.contains("Request Feedback").first().click();
    cy.url().should("include", "/feedback/request");
    cy.url().should("include", "for=");
    cy.contains("Feedback Request for");

    cy.contains("Survey 1").click();
    cy.url().should("include", "template=");
    cy.contains("Next").click();

    cy.url().should("include", "step=2");
    cy.get(".member-card").click({multiple: true});
    cy.url().should("match", /from=*&*/);
    cy.contains("Next").click();

    cy.url().should("include", "step=3");

    cy.contains("Send Date:").click();
    cy.contains("OK").click();
    cy.url().should("include", "send=");
    cy.contains("Due Date:").click();
    cy.contains("OK").click();
    cy.url().should("include", "due=");

    cy.contains("Submit").click();
    cy.contains("Feedback scheduled for today");
    cy.url().should("include", "/feedback/request/confirmation");
  });
});