import React from 'react';
import SettingsString from './string';
import '../../../pages/SettingsPage.css';

export default {
  component: SettingsString,
  title: 'Check Ins/Settings',
  decorators: [
    SettingsString => (
      <div className="settings-page">
        <SettingsString />
      </div>
    )
  ]
};

const Template = args => {
  return <SettingsString {...args} />;
};

export const String = Template.bind({});
String.args = {
  label: 'String Control',
  description: 'A control to hold a string value'
};
