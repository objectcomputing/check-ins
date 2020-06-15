import React from "react";
import Header from "./Header";
import renderer from "react-test-renderer";

it("renders title", () => {
  const tree = renderer.create(<Header title="Ze title" />).toJSON();
  expect(tree).toMatchSnapshot();
});
