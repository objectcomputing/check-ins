import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import Pulse from './Pulse';

it('renders correctly', () => {
  const component = render(
    <Pulse
      comment="Just testing"
      score={2}
      setComment={() => {}}
      setScore={() => {}}
      title="How are you feeling about work today? (*)"
    />
  );
  expect(component.baseElement).toMatchSnapshot();
});

it('calls setComment', () => {
  const setComment = vi.fn();
  render(
    <Pulse
      comment="test"
      score={2}
      setComment={setComment}
      setScore={() => {}}
    />
  );
  const input = screen.getByTestId('comment-input').querySelector('textarea');
  const text = 'new comment';
  fireEvent.change(input, { target: { value: text } });
  expect(setComment).toHaveBeenCalledWith(text);
});

it('calls setScore', () => {
  const setScore = vi.fn();
  render(
    <Pulse comment="" score={2} setComment={() => {}} setScore={setScore} />
  );
  const button = screen.getByTestId('score-button-4');
  fireEvent.click(button);
  expect(setScore).toHaveBeenCalledWith(4);
});
