import React from 'react';
import SettingsNumber from './number';
import '../../../pages/SettingsPage.css';

export default {
  component: SettingsNumber,
  title: 'Check Ins/Settings',
  decorators: [
    SettingsNumber => (
      <div className="settings-page">
        <SettingsNumber />
      </div>
    )
  ]
};

const Template = args => {
  return <SettingsNumber {...args} />;
};

export const Number = Template.bind({});
Number.args = {
  label: 'Number Control',
  description: 'A control to hold a numerical value'
};
