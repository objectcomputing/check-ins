import React from "react";
import Profile from "./Profile";
import renderer from "react-test-renderer";

it("renders correctly", () => {
  const tree = renderer.create(<Profile />).toJSON();
  expect(tree).toMatchSnapshot();
});

it("renders image_url", () => {
  const tree = renderer
    .create(<Profile image_url={"http://someurl.com/das.png"} />)
    .toJSON();
  expect(tree).toMatchSnapshot();
});
