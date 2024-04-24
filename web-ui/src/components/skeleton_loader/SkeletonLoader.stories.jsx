import React from 'react';
import SkeletonLoader from './SkeletonLoader';

export default {
  component: SkeletonLoader,
  title: 'Check Ins/SkeletonLoader'
};

const Template = args => {
  return <SkeletonLoader {...args} />;
};

export const TeamLoader = Template.bind({});
TeamLoader.args = {
  type: 'team'
};

export const GuildLoader = Template.bind({});
GuildLoader.args = {
  type: 'guild'
};

export const PeopleLoader = Template.bind({});
PeopleLoader.args = {
  type: 'people'
};

export const FeedbackRequestLoader = Template.bind({});
FeedbackRequestLoader.args = {
  type: 'feedback_requests'
};

export const RecievedRequestLoader = Template.bind({});
RecievedRequestLoader.args = {
  type: 'received_requests'
};

export const FeedbackResponsesLoader = Template.bind({});
FeedbackResponsesLoader.args = {
  type: 'view_feedback_responses'
};
