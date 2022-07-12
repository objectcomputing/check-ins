import DocumentIndicator from "./DocumentIndicator";
import { render, screen } from "@testing-library/react";

describe("Mock Database Test", () => {
  let mockDatabase = {
    count: 2,
    next: null,
    previous: null,
    results: [
      {
        url: "https://ocitest.signrequest.com/api/v1/documents/152408ef-4b71-40a4-9416-92011a443450/",
        uuid: "152408ef-4b71-40a4-9416-92011a443450",
        name: "Ryu Daniel OCI Engineer (Paid) Intern EA (1).pdf",
        status: "se",
        signrequest: {
          signers: [
            {
              email: "ryud@objectcomputing.com",
              display_name: "Daniel Ryu (ryud@objectcomputing.com)",
              first_name: "Daniel",
              last_name: "Ryu",
            },
            {
              email: "d97shryu@gmail.com",
              display_name: "d97shryu@gmail.com",
              first_name: "",
              last_name: "",
            },
          ],
        },
      },
    ],
  };
  test("returns the correct document title", () => {
    render(<DocumentIndicator documentRequest={mockDatabase} />);
    async function getTitle() {
      let documentTitle = await screen.findByText(
        "Ryu Daniel OCI Engineer (Paid) Intern EA (1).pdf"
      );
      expect(documentTitle).toBeInTheDocument();
    }
    getTitle();
  });
});
