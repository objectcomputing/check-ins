import React from "react";

describe("make a feedback request", () => {
  it("checks truth", () => {
    expect(true).to.equal(true);
  });

  it("navigates to the homepage", () => {
    cy.visit("localhost:3000");
    cy.contains("Location");
  });
});