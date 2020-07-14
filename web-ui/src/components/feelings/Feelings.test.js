import React from "react";
import Feelings from "./Feelings";

it("renders correctly", () => {
  snapshot(<Feelings />);
});

const message = "le test";

it("renders message correctly", () => {
  snapshot(<Feelings message={message} />);
});

const onSelect = () => {
  console.log("selected");
};

it("renders onSelect correctly", () => {
  snapshot(<Feelings onSelect={onSelect} />);
});
