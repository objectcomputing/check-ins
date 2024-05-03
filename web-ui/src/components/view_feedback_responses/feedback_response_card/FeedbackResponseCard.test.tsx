import React from 'react';
// @ts-ignore
import FeedbackResponseCard from './FeedbackResponseCard';
// @ts-ignore
import { AppContextProvider } from '../../../context/AppContext';

vi.mock('@mui/material/Slider', () => {
  return {
    default: () => (props: any) => {
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
    }
  };
});

describe('FeedbackResponseCard', () => {
  it('renders correctly for text responses', () => {
    // @ts-ignore
    snapshot(
      <AppContextProvider>
        <FeedbackResponseCard
          responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
          inputType="TEXT"
          answer="I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!"
          sentiment={0.8}
        />
      </AppContextProvider>
    );
  });

  it('renders correctly for radio button responses', () => {
    // @ts-ignore
    // @ts-ignore
    snapshot(
      <AppContextProvider>
        <FeedbackResponseCard
          responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
          inputType="RADIO"
          answer="Yes"
          sentiment={1}
        />
      </AppContextProvider>
    );
  });

  it('renders correctly for slider responses', () => {
    // @ts-ignore
    snapshot(
      <AppContextProvider>
        <FeedbackResponseCard
          responderId="01b7d769-9fa2-43ff-95c7-f3b950a27bf9"
          inputType="SLIDER"
          answer="Neither Agree nor Disagree"
          sentiment={0.5}
        />
      </AppContextProvider>
    );
  });
});
