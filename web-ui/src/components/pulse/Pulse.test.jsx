import React from 'react';
import { configure, fireEvent, render, screen } from '@testing-library/react';
import { act } from '@testing-library/react-hooks';
import '@testing-library/jest-dom';
import Pulse from './Pulse';

it('renders correctly', () => {
  snapshot(
    <Pulse
      comment="Just testing"
      score={2}
      setComment={() => {}}
      setScore={() => {}}
    />
  );
});

it('calls setComment', async () => {
  beforeEach(() => {
    configure({ testIdAttribute: 'data-testid' });
  });

  const setComment = vi.fn();
  await act(async () => {
    const { getByTestId } = render(
      <Pulse
        comment="test"
        score={2}
        setComment={setComment}
        setScore={() => {}}
      />
    );
  });
  const text = 'new comment';

  const textArea = await screen.getByTestId('comment-input').querySelector('textarea'); // The field rendered is a textarea not an input
  expect(textArea).toBeTruthy();

  fireEvent.change(textArea, { target: { value: text } });
  expect(setComment).toHaveBeenCalledWith(text);
});

it('calls setScore', async () => {
  beforeEach(() => {
    configure({ testIdAttribute: 'data-testid' });
  });
  const setScore = vi.fn();
  await act(async () => {
    const { getByTestId } = render(
      <Pulse comment="" score={4} setComment={() => {}} setScore={setScore} />
    );
  });
  const button = await screen.getByTestId('score-button-3');

  fireEvent.click(button);
  expect(setScore).toHaveBeenCalledWith(3); // This is only happening 3 times. There may be an indexing problem with the Pulse.jsx component for these buttons.
});
