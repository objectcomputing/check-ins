import React, { useContext, useEffect, useState } from 'react';
import { debounce } from 'lodash/function';
import { AppContext } from '../context/AppContext';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import {getAllOptions, updateSetting, getAll } from '../api/settings';
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
  const {state} = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [settingsControls, setSettingsControls] = useState([]);
  const [settingsDatabaseValues, setSettingsDatabaseValues] = useState([]);

  const realStoreSetting = async (name, value, csrf) => await updateSetting(name, value, csrf);

  const storeSetting = debounce(realStoreSetting, 1500);

  useEffect(() => {
    const fetchData = async () => {
      const expectedSettings = (await getAllOptions()).payload.data;
      const allSettingsValuesFromDB = (await getAll()).payload.data;
      setSettingsControls(expectedSettings);
      setSettingsDatabaseValues(allSettingsValuesFromDB);

    };

    fetchData();
  }, []);


  const mergeControlValues = (originalSettings, newValues) => {
    console.log("originalSettings:" + originalSettings);
    console.log("newValues: " + newValues);
    const mergedSettings = originalSettings.map( originalSetting => {
      let newValue = newValues?.find((element) => element.name == originalSetting.name);
      if (newValue) {
        const combinedObject = {...originalSetting, ...newValue}
        return combinedObject
      }
      return {...originalSetting, createNew: true};
    });

    return mergedSettings;
  }

  const originalSettings = settingsControls
  const newValues = settingsDatabaseValues

  const mergedSettings = mergeControlValues(originalSettings, newValues)



  /*
    for specific settings, add a handleFunction to the settings object
    format should be handleSetting and then add it to the handlers object
    with the setting name as the key
  */
  const handleLogoUrl = file => {
    if (csrf) {
      console.log("In handleLogoUrl")
      // TODO: need to have a storage bucket to upload the file to
    }
  };
//TODO: Maybe remove since it's in string.jsx
  const handleStringChange = (e) => {
    if (!csrf) {
      return;
    }
    console.log("In handleStringChange", this)
    const {value} = e.target;
    let name = e.target.id.toUpperCase();

    // this.setState({
    //   stringValue: value
    // });
    // storeSetting({...setting, value: settingValue}, csrf);
    storeSetting(name, value, csrf);
    // updateSetting(name, value);
  };

  const handlers = {
    LOGO_URL: handleLogoUrl,
    // FROM_NAME: handleStringChange

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
        return {...setting, handleChange: handler};
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
  // const updatedValues = addValuesToSettings(settingsControls)
  const updatedSettingsControls = addHandlersToSettings(mergedSettings);
  // const updatedValues = addValuesToSettings(updatedSettingsControls)
  // setFinalControlValues(updatedSettingsControls)

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
