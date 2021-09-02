import React, {useRef} from 'react';
import MarkdownNote from './MarkdownNote';


export default {
  component: MarkdownNote,
  title: 'Check Ins/MarkdownNote',
}

const Template = (args) => {
  return <MarkdownNote {...args} />;
}


export const NoPropsPassed = Template.bind({});

export const ControlledComponent = Template.bind({});
ControlledComponent.args = {
  value: "<h1>Controlled Component</h1><p>Can pass in a value and onChange prop to make the component controlled</p>",
  onChange: () => {console.log('handle change')}
}

export const CustomStyling = Template.bind({});
CustomStyling.args = {
  value: "<h1>Configure the component with a style prop.</h1><h1>Here we pass in a defined height.</h1><h1>This sets the overflow to auto for the text area.</h1><h1>overflow</h1><h1>overflow</h1><h1>overflow</h1>",
  style: {height: "175px"},
}

export const ReadOnly = Template.bind({});
ReadOnly.args = {
  readOnly: true,
  defaultValue: "<h1>Cannot edit</h1>"
}



