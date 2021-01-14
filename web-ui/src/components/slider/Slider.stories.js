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

};
