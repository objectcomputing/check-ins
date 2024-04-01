import React from "react";
import Feelings from "./Feelings";
import { render, screen, fireEvent } from "@testing-library/react";

it("renders correctly", () => {
  snapshot(<Feelings />);
});

const message = "le test";

it("renders message correctly", () => {
  snapshot(<Feelings message={message} />);
});

it("calls onSelect correctly", () => {
  const onSelect = vi.fn();
  render(<Feelings onSelect={onSelect} />);

  const input = screen.getByTestId('feelings-input-0');
  fireEvent.click(input);

  expect(onSelect.mock.calls.length).toBe(1);
});
