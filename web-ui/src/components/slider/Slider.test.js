import React from "react";
import Slider from "./Slider";
import renderer from "react-test-renderer";
import { mount } from "enzyme";

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

it("value change is reported to the handler correctly", () => {
  const onChange = jest.fn();

  const wrapper = mount(
    <Slider title="Some skill" onChange={onChange} />
  );
  const slider = wrapper.find(Slider).at(0);
  slider.simulate("change", { target: { value: 5 } });
  slider.simulate("change", { target: { value: 4 } });
  setTimeout(() => expect(onChange.mock.calls.length).toBe(2), 0);
});

it("value change is reported to all handlers correctly", () => {
  const onChange = jest.fn();
  const onChangeCommitted = jest.fn();

  const wrapper = mount(
    <Slider title="Some skill" onChange={onChange} onChangeCommitted={onChangeCommitted} />
  );

  const slider = wrapper.find(Slider).at(0);
  slider.simulate("change", { target: { value: 5 } });
  slider.find(".MuiSlider-mark").at(2).simulate("click", { target: { value: 3 } });
  setTimeout(() => {
    expect(onChange.mock.calls.length).toBe(2);
    expect(onChangeCommitted.mock.calls.length).toBe(1);
  }, 0);
});
