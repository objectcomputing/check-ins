import React from "react";
import DiscreteSlider from "./DiscreteSlider";
import { mount } from "enzyme";

it("renders slider with title", () => {
  snapshot(<DiscreteSlider title="Some skill" />, {
    createNodeMock: (element) => {
      if (element.type === 'div') {
        return {
          addEventListener: jest.fn(),
        };
      }
    },
  });
});

it("value change is reported to the handler correctly", () => {
  const onChange = jest.fn();

  const wrapper = mount(
    <DiscreteSlider title="Some skill" onChange={onChange} />
  );
  const slider = wrapper.find(DiscreteSlider).at(0);
  slider.simulate("change", { target: { value: 5 } });
  slider.simulate("change", { target: { value: 4 } });
  setTimeout(() => expect(onChange.mock.calls.length).toBe(2), 0);
});

it("value change is reported to all handlers correctly", () => {
  const onChange = jest.fn();
  const onChangeCommitted = jest.fn();

  const wrapper = mount(
    <DiscreteSlider title="Some skill" onChange={onChange} onChangeCommitted={onChangeCommitted} />
  );

  const slider = wrapper.find(DiscreteSlider).at(0);
  slider.simulate("change", { target: { value: 5 } });
  slider.find("span.MuiSlider-mark").at(2).simulate("click", { target: { value: 3 } });
  setTimeout(() => {
    expect(onChange.mock.calls.length).toBe(2);
    expect(onChangeCommitted.mock.calls.length).toBe(1);
  }, 0);
});
