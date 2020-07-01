import React from "react";
import Profile from "./Profile";

it("renders correctly", () => {
  snapshot(<Profile />);
});

it("renders image_url", () => {
  snapshot(<Profile image_url="http://someurl.com/das.png" />);
});
