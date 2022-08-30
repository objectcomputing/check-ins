import React from "react";
import GuideLink from "./GuideLink";
import { MemoryRouter } from 'react-router-dom';

it("renders correctly", () => {
  snapshot(<MemoryRouter>
    <GuideLink
      document={{
        name: "myFileName",
        url: "/pdfs/myFileName.pdf"
      }}
      draggable={false}
    />
  </MemoryRouter>);
});
