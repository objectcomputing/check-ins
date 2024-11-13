import React from 'react';
import SettingsColor from './color';
import '../../../pages/SettingsPage.css';

export default {
  component: SettingsColor,
  title: 'Check Ins/Settings',
  decorators: [
    SettingsColor => (
      <div className="settings-page">
        <SettingsColor />
      </div>
    )
  ]
};

const Template = args => {
  return <SettingsColor {...args} />;
};

export const Color = Template.bind({});
Color.args = {
  name: 'Color Control',
  description: 'A control to hold a color value',
  value: '#2c519e',
  handleChange: event => console.log(`COLOR: ${event.target.value}`)
};
