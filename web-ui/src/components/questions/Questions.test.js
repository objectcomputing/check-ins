import React from "react";
import Questions from "./Questions";
import { mount } from "enzyme";

const questions = [
  { question: "What's up?", id: 1 },
  { question: "Doc", id: 2 },
];

it("renders questions correctly", () => {
  snapshot(<Questions questions={questions} />);
});

it("renders onSelect correctly", () => {
  const onAnswer = jest.fn();
  const wrapper = mount(
    <Questions onAnswer={onAnswer} questions={questions} />
  );
  const find = wrapper.find("#questions-input-1");

  find.at(0).simulate("input", { target: { value: "sometext" } });
  expect(onAnswer.mock.calls.length).toBe(1);
});
