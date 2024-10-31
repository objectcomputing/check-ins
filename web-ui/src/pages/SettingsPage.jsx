import React, { useContext, useEffect, useState } from 'react';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import { Button, Typography } from '@mui/material';
import {
  SettingsBoolean,
  SettingsColor,
  SettingsFile,
  SettingsNumber,
  SettingsString
} from '../components/settings';
import { putOption, postOption, getAllOptions } from '../api/settings';
import {
  selectCsrfToken,
  selectHasViewSettingsPermission,
  selectHasAdministerSettingsPermission,
  noPermission
} from '../context/selectors';
import { titleCase } from '../helpers/strings';
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
      const allOptions = selectHasViewSettingsPermission(state) ||
                         selectHasAdministerSettingsPermission(state) ?
                            (await getAllOptions()).payload.data : [];

      if (allOptions) {
        // Sort the options by category, store them, and upate the state.
        setSettingsControls(
          allOptions.sort((l, r) => {
            if (l.category === r.category) {
              return l.name.localeCompare(r.name);
            } else {
              return l.category.localeCompare(r.category);
            }
          })
        );
      }
    };
    if (csrf) {
      fetchData();
    }
  }, [state, csrf]);

  // For specific settings, add a handleFunction to the settings object.
  // Format should be handleSetting and then add it to the handlers object
  // with the setting name as the key.
  const handleLogoUrl = file => {
    if (csrf) {
      // TODO: Need to upload the file to a storage bucket...
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'warning',
          toast: "The Logo URL setting has yet to be implemented.",
        }
      });
    }
  };

  const keyedHandler = (key, event) => {
    if (handlers[key]) {
      handlers[key].setting.value = event.target.value;
      setState({update: true});
    }
  };

  const handlePulseEmailFrequency = (event) => {
    keyedHandler('PULSE_EMAIL_FREQUENCY', event);
  };

  const handlers = {
    // File handlers do not modify settings values and, therefore, do not
    // need to keep a reference to the setting object.  However, they do need
    // a file reference object.
    LOGO_URL: {
      onChange: handleLogoUrl,
      setting: fileRef,
    },

    // All others need to provide an `onChange` method and a `setting` object.
    PULSE_EMAIL_FREQUENCY: {
      onChange: handlePulseEmailFrequency,
      setting: undefined,
    },
  };

  const addHandlersToSettings = settings => {
    return settings ? settings.map(setting => {
      const handler = handlers[setting.name.toUpperCase()];
      if (handler) {
        if (setting.type.toUpperCase() === 'FILE') {
          return {
            ...setting,
            handleFunction: handler.onChange,
            fileRef: handler.setting,
          };
        }

        handler.setting = setting;
        return { ...setting, handleChange: handler.onChange };
      }

      console.warn(`WARNING: No handler for ${setting.name}`);
      return setting;
    }) : [];
  };

  const save = async () => {
    let errors;
    let saved = 0;
    for(let key of Object.keys(handlers)) {
      const setting = handlers[key].setting;
      // The settings controller does not allow blank values.
      if (setting?.name && `${setting.value}` != "") {
        let res;
        if (setting.id) {
          res = await putOption({ name: setting.name,
                                  value: setting.value }, csrf);
        } else {
          res = await postOption({ name: setting.name,
                                   value: setting.value }, csrf);
          if (res?.payload?.data) {
            setting.id = res.payload.data.id;
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
      } else {
        console.warn(`WARNING: ${setting.name} not sent to the server`);
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
  const categories = {};

  return (selectHasViewSettingsPermission(state) ||
          selectHasAdministerSettingsPermission(state)) ? (
    <div className="settings-page">
      {updatedSettingsControls.map((componentInfo, index) => {
        const Component = componentMapping[componentInfo.type.toUpperCase()];
        const info = {...componentInfo, name: titleCase(componentInfo.name)};
        if (categories[info.category]) {
          return <Component key={index} {...info} />;
        } else {
          categories[info.category] = true;
          return (
            <>
            <Typography data-testid={info.category}
                        variant="h4"
                        sx={{textDecoration: 'underline'}}
                        display="inline">{titleCase(info.category)}</Typography>
            <Component key={index} {...info} />
            </>
          );
        }
      })}
      {settingsControls && settingsControls.length &&
       selectHasAdministerSettingsPermission(state) &&
      <div className="buttons">
        <Button
          color="primary"
          onClick={save}>
          Save
        </Button>
      </div>
      }
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

SettingsPage.displayName = displayName;

export default SettingsPage;
