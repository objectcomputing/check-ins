import React, { useContext, useEffect, useState } from 'react';
import { AppContext } from '../context/AppContext';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import { getAllOptions } from '../api/settings';
import { selectCsrfToken } from '../context/selectors';
import './SettingsPage.css';

const displayName = 'SettingsPage';

const componentMapping = {
  BOOLEAN: SettingsBoolean,
  COLOR: SettingsColor,
  FILE: SettingsFile,
  NUMBER: SettingsNumber,
  STRING: SettingsString
};

const SettingsPage = () => {
  const fileRef = React.useRef(null);
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [settingsControls, setSettingsControls] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const allOptions = (await getAllOptions()).payload.data;
      setSettingsControls(allOptions);
    };
    fetchData();
  }, []);

  /* 
    for specific settings, add a handleFunction to the settings object
    format should be handleSetting and then add it to the handlers object
    with the setting name as the key
  */
  const handleLogoUrl = file => {
    if (csrf) {
      // setFiles([...files, file]);
      // addFile(file);
      console.log(file);
    }
  };

  const handlers = {
    LOGO_URL: handleLogoUrl
  };

  const addHandlersToSettings = settings => {
    return settings.map(setting => {
      const handler = handlers[setting.name.toUpperCase()];
      if (handler && setting.type.toUpperCase() === 'FILE') {
        return {
          ...setting,
          handleFunction: handler,
          fileRef: fileRef
        };
      }
      if (handler) {
        return { ...setting, handleFunction: handler };
      }
      return setting;
    });
  };

  /**
   * @typedef {Object} Controls
   * @property {ComponentName} componentName - The name of the component.
   *
   * @typedef {('SettingsBoolean'|'SettingsColor'|'SettingsFile'|'SettingsNumber'|'SettingsString')} ComponentName
   */

  /** @type {Controls[]} */
  const updatedSettingsControls = addHandlersToSettings(settingsControls);

  return (
    <div className="settings-page">
      {updatedSettingsControls.map((componentInfo, index) => {
        const Component = componentMapping[componentInfo.type.toUpperCase()];
        return <Component key={index} {...componentInfo} />;
      })}
    </div>
  );
};

SettingsPage.displayName = displayName;

export default SettingsPage;
