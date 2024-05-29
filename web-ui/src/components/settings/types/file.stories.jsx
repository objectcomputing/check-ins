import React from 'react';
import SettingsFile from './file';
import '../../../pages/SettingsPage.css';

const fileRef = null;

export default {
  component: SettingsFile,
  title: 'Check Ins/Settings',
  decorators: [
    SettingsFile => (
      <div className="settings-page">
        <SettingsFile />
      </div>
    )
  ]
};

const Template = args => {
  return <SettingsFile {...args} />;
};

export const File = Template.bind({});
File.args = {
  name: 'File Control',
  description: 'A control to upload a File',
  fileRef,
  handleFile: event => console.log(`File: ${event.target.value}`)
};
