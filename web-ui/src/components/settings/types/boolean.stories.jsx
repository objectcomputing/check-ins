import React from 'react';
import SettingsBoolean from './boolean';
import '../../../pages/SettingsPage.css';

export default {
  component: SettingsBoolean,
  title: 'Check Ins/Settings',
  decorators: [
    SettingsBoolean => (
      <div className="settings-page">
        <SettingsBoolean />
      </div>
    )
  ]
};

const Template = args => {
  return <SettingsBoolean {...args} />;
};

export const Boolean = Template.bind({});
Boolean.args = {
  name: 'Boolean Control',
  description: 'A control to hold a boolean value',
  handleChange: event => console.log(`BOOLEAN ${event.target.checked}`)
};
