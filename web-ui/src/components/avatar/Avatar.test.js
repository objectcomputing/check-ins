import React from "react";
import Avatar from "./Avatar";
import renderer from "react-test-renderer";

it("renders correctly", () => {
  const tree = renderer.create(<Avatar />).toJSON();
  expect(tree).toMatchSnapshot();
});

it("renders image_url", () => {
  const tree = renderer
    .create(<Avatar image_url={"http://someurl.com/das.png"} />)
    .toJSON();
  expect(tree).toMatchSnapshot();
});
