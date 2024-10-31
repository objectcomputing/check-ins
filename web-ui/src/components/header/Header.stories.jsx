import React from 'react';

import Header from './Header';

export default {
  title: 'Check-Ins/Header',
  component: Header
};

const Template = args => <Header {...args} />;

export const Simple = Template.bind({});
Simple.args = {
  title: 'Test Title'
};
