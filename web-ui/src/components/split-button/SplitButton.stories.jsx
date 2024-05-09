import React from 'react';
import SplitButton from './SplitButton';

export default {
  title: 'Check Ins/SplitButton',
  component: SplitButton
};

const Template = args => <SplitButton {...args} />;
const options = ['One', 'Two', 'Three'];

export const SplitButtonNoToggle = Template.bind({});
SplitButtonNoToggle.args = {
  toggleOnSelect: false,
  onClick: (e, index) => window.alert(`You clicked ${options[index]}`),
  options: options
};
export const SplitButtonToggle = Template.bind({});
SplitButtonToggle.args = {
  toggleOnSelect: true,
  onClick: (e, index) => window.alert(`You clicked ${options[index]}`),
  options: options
};
