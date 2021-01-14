import React from 'react';
import Slider from './Slider';

export default {
  title: 'Check Ins/Slider',
  component: Slider
}
const Template = (args) => 
    <Slider {...args} />;

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
