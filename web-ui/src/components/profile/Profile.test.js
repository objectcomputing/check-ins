import React from "react";
import Profile from "./Profile";
import renderer from "react-test-renderer";

it("renders correctly", () => {
  snapshot(<Profile />);
});

it("renders image_url", () => {
  snapshot(<Profile image_url="http://someurl.com/das.png" />);
});
