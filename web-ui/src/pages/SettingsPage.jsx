import React, { useState } from 'react';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import './SettingsPage.css';

const displayName = 'SettingsPage';

const componentMapping = {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
};

const SettingsPage = () => {
  const hiddenFileInput = React.useRef(null);

  const handleFile = file => {
    if (csrf) {
      setFiles([...files, file]);
      addFile(file);
    }
  };

  /**
   * @typedef {Object} Controls
   * @property {ComponentName} componentName - The name of the component.
   *
   * @typedef {('SettingsBoolean'|'SettingsColor'|'SettingsFile'|'SettingsNumber'|'SettingsString')} ComponentName
   */

  /** @type {Controls[]} */
  const settingsControls = [
    {
      componentName: 'SettingsFile',
      title: 'Branding',
      description: 'Upload your logo file',
      fileRef: hiddenFileInput,
      handleFile
    },
    {
      componentName: 'SettingsString',
      label: 'String Control',
      description: 'A control to hold a string value',
      handleChange: event => console.log(`STRING: ${event.target.value}`)
    },
    {
      componentName: 'SettingsNumber',
      label: 'Number Control',
      description: 'A control to hold a number value',
      handleChange: event => console.log(`NUMBER: ${event.target.value}`)
    },
    {
      componentName: 'SettingsBoolean',
      label: 'Boolean Control',
      description: 'A control to hold a boolean value',
      handleChange: event => console.log(`BOOLEAN ${event.target.checked}`)
    },
    {
      componentName: 'SettingsColor',
      label: 'Color Control',
      description: 'A control to hold a color value',
      handleChange: event => console.log(`COLOR: ${event.target.value}`)
    }
  ];

  // TODO: Remove this, and settingsControl once the UI types are reviewed and accepted
  if (process.env.NODE_ENV !== 'development') {
    settingsControls.length = 0;
  }

  return (
    <div className="settings-page">
      {settingsControls.map((componentInfo, index) => {
        const Component = componentMapping[componentInfo.componentName];
        return <Component key={index} {...componentInfo} />;
      })}
    </div>
  );
};

SettingsPage.displayName = displayName;

export default SettingsPage;
