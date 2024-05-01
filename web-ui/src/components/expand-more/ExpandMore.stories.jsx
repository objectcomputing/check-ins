import React from 'react';
import ExpandMore from './ExpandMore';

export default {
  title: 'Check Ins/ExpandMore',
  component: ExpandMore
};
const Template = args => <ExpandMore {...args} />;

export const DefaultExpandMore = Template.bind({});
DefaultExpandMore.args = {};

// Expanded
export const ExpandedExpandMore = Template.bind({});
ExpandedExpandMore.args = {
  expand: true
};

// Collapsed
export const CollapsedExpandMore = Template.bind({});
CollapsedExpandMore.args = {
  expand: false
};
