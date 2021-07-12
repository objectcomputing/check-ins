
describe("make a feedback request", () => {
  it("checks truth", () => {
    expect(true).to.equal(true);
  });

  beforeEach("logs in", () => {
    const email = "user@objectcomputing.com";
    const role = "SUPER";
    cy.loginByCsrf(email, role).then((res) => {
      expect(res.status).to.eq(200);
    });
  });

  it("navigates to the homepage", () => {
    cy.visit("localhost:3000");
    cy.contains("CHECK-INS").click();
  });
});