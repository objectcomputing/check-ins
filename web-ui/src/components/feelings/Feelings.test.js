import React from "react";
import Feelings from "./Feelings";
import { mount } from "enzyme";

it("renders correctly", () => {
  snapshot(<Feelings />);
});

const message = "le test";

it("renders message correctly", () => {
  snapshot(<Feelings message={message} />);
});

it("calls onSelect correctly", () => {
  const onSelect = jest.fn();
  const wrapper = mount(<Feelings onSelect={onSelect} />);
  const find = wrapper.find("#feelings-input-0");
  find.at(0).simulate("click");
  expect(onSelect.mock.calls.length).toBe(1);
});
