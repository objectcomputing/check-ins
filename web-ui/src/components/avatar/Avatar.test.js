import React from "react";
import Avatar from "./Avatar";
import renderer from "react-test-renderer";

it("renders correctly", () => {
  snapshot(<Avatar />);
});

it("renders image_url", () => {
  snapshot(<Avatar image_url={"http://someurl.com/das.png"} />);
});
