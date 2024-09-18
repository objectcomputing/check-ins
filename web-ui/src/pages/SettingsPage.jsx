import React, { useContext, useEffect, useState } from 'react';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import { Button } from '@mui/material';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import { putOption, postOption, getAllOptions } from '../api/settings';
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
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [settingsControls, setSettingsControls] = useState([]);
  const [update, setState] = useState();

  useEffect(() => {
    const fetchData = async () => {
      // Get the options from the server
      const allOptions = (await getAllOptions()).payload.data;

      // If the option has a valid UUID, then the setting already exists.
      // This information is necessary to know since we must use POST to
      // create new settings and PUT to modify existing settings.
      for (let option of allOptions) {
        option.exists = option.id != '00000000-0000-0000-0000-000000000000';
      }

      // Store them and upate the state.
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
      // TODO: need to have a storage bucket to upload the file to
    }
  };

  const handlePulseEmailFrequency = (event) => {
    const key = 'PULSE_EMAIL_FREQUENCY';
    if (handlers[key]) {
      handlers[key].setting.value = event.target.value;
      setState({update: true});
    }
  };

  const handlers = {
    // File handlers do not modify settings values and, therefore, do not
    // need to keep a reference to the setting object.
    LOGO_URL: handleLogoUrl,

    // All others need to provide an `onChange` method and a `setting` object.
    PULSE_EMAIL_FREQUENCY: {
      onChange: handlePulseEmailFrequency,
      setting: undefined,
    },
  };

  const addHandlersToSettings = settings => {
    return settings.map(setting => {
      const handler = handlers[setting.name.toUpperCase()];
      if (handler) {
        if (setting.type.toUpperCase() === 'FILE') {
          return {
            ...setting,
            handleFunction: handler,
            fileRef: fileRef
          };
        }

        handler.setting = setting;
        return { ...setting, handleChange: handler.onChange };
      }

      console.log(`WARNING: No handler for ${setting.name}`);
      return setting;
    });
  };

  const save = async () => {
    let errors;
    let saved = 0;
    for( let key of Object.keys(handlers)) {
      const setting = handlers[key].setting;
      // The settings controller does not allow blank values.
      if (setting && setting.value) {
        let res;
        if (setting.exists) {
          res = await putOption({ name: setting.name,
                                  value: setting.value }, csrf);
        } else {
          res = await postOption({ name: setting.name,
                                   value: setting.value }, csrf);
          if (res?.payload?.data) {
            setting.exists = true;
          }
        }
        if (res?.error) {
          const error = res?.error?.message;
          if (errors) {
            errors += "\n" + error;
          } else {
            errors = error;
          }
        }
        if (res?.payload?.data) {
          saved++;
        }
      }
    }

    if (errors) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: errors,
        }
      });
    } else if (saved > 0) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Settings have been saved',
        }
      });
    }
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
      <div className="buttons">
        <Button
          color="primary"
          onClick={save}>
          Save
        </Button>
      </div>
    </div>
  );
};

SettingsPage.displayName = displayName;

export default SettingsPage;
