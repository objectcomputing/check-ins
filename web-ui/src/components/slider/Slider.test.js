import React from "react";
import Slider from "./Slider";
import renderer from "react-test-renderer";

jest.mock('react-dom', () => ({
  findDOMNode: jest.fn()
}))

jest.mock('react', () => {
  const originReact = jest.requireActual('react');
  const mUseRef = jest.fn(() => ({current: {update: jest.fn()}}));
  return {
    ...originReact,
    useRef: mUseRef,
  };
});

it("renders slider with title", () => {
  snapshot(<Slider title="Some skill" />);
});

// it("renders slider correctly", () => {
//   const wrapper = mount(
//     <Slider title="Some skill" />
//   );
//   const find = wrapper.find("#questions-input-1");

//   find.at(0).simulate("input", { target: { value: "sometext" } });
//   expect(onAnswer.mock.calls.length).toBe(1);
// });
