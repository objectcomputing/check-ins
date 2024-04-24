import React from 'react';
import TemplateCard from './TemplateCard';

export default {
  title: 'Check Ins/TemplateCard',
  component: TemplateCard
};
const Template = args => <TemplateCard {...args} />;

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {};

export const PeerFeedbackTemplate = Template.bind({});
PeerFeedbackTemplate.args = {
  templateName: 'Peer Feedback',
  description: 'A simple feedback template',
  creator: 'Bob Jones',
  questions: [
    'Test 1',
    'Test 2',
    'Test 3',
    'Test 4',
    'Test 5',
    'Test 6',
    'Test 7',
    'Test 8'
  ]
};
