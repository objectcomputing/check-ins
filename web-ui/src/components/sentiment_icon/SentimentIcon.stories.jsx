import React from 'react';
import SentimentIcon from './SentimentIcon';
import { AppContextProvider } from '../../context/AppContext';

export default {
  title: 'Check Ins/SentimentIcon',
  component: SentimentIcon,
  decorators: [
    SentimentIcon => (
      <AppContextProvider>
        <SentimentIcon />
      </AppContextProvider>
    )
  ]
};

const sentimentData = {
  sentimentScore: 0
};

const Template = args => {
  return <SentimentIcon {...args} />;
};

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {
  ...sentimentData
};
