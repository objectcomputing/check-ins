import React from "react";
import DiscreteSlider from "./DiscreteSlider";
import { render, screen, fireEvent } from "@testing-library/react";

import {jest} from "@jest/globals";

jest.mock("@mui/material/Slider", () => (props: any) => {
  const { onChange, 'data-testid': testId, ...rest } = props;

  return (
    <input
      data-testid={testId}
      type="range"
      onChange={event => {
        onChange(null, parseInt(event.target.value, 10));
      }}
      {...rest}
    />
  );
});

it("renders slider with title", () => {
  snapshot(<DiscreteSlider title="Some skill" />, {
    createNodeMock: (element) => {
      if (element.type === "div") {
        return {
          addEventListener: jest.fn(),
        };
      }
    },
  });
});
