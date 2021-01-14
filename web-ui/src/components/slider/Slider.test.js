import React from "react";
import Slider from "./Slider";
import renderer from "react-test-renderer";

it("renders slider", () => {
  snapshot(<Slider />);
});

it("renders slider with title", () => {
  snapshot(<Slider title="Some skill" />);
});
