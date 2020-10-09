import React from 'react';
import CheckinCycle from './CheckinCycle';

export default {
  title: 'CheckinCycle',
  component: CheckinCycle,
};

const Template = (args) => <CheckinCycle {...args} />;

export const NoDimensions = Template.bind({});

export const SetDimensions = Template.bind({});
SetDimensions.args = {
    style: {height: "30rem", width: "300rem"}
};

