import React from 'react';
import DiscreteSlider from './DiscreteSlider';

export default {
  title: 'Check Ins/DiscreteSlider',
  component: DiscreteSlider
}
const Template = (args) => 
    <DiscreteSlider {...args} />;

export const DefaultSlider = Template.bind({});
DefaultSlider.args = {
  title: "Some skill",
  lastUsed: undefined
};

export const SliderWithChangeHandler = Template.bind({});
SliderWithChangeHandler.args = {
  onChangeCommitted: (event, value) => {
    window.alert(value);
  }
}

export const SliderWithLastUsed = Template.bind({});
SliderWithLastUsed.args = {
  title: "Other Skill",
  lastUsed: "Currently Used"
}
